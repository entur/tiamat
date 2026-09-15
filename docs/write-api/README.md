# Reference client for the stop place write API

`write-api.http` is a worked example of the write API. It creates a stop place, updates it, fails an
update on purpose, and deletes it. Run it request by request from the gutter in IntelliJ, or read it
as a description of the wire protocol and translate it into whatever you build in.

This is documentation that runs. It is not a test, and nothing in the build runs it.

## What to notice

**`202` does not mean the write happened.** It means the API took the job. The write happens later,
and the outcome lives on `GET /write/jobs/{jobId}`. Every request in this file that changes something
is followed by a request that reads what it did.

**An update sends the version you read.** If the stop place is at version 4, you send `version="4"`
and the system writes version 5. You never choose the next version. Request 3 in this file sends the
version that request 2 read, and that handoff is the whole contract.

**A write that cannot succeed is still accepted.** Request 5 sends a version that is no longer
current. It answers `202`, and the job fails afterwards with `STALE_VERSION`. A client that treats
`202` as success never finds out.

**A failed write changes nothing.** Send request 5 twice and `currentVersion` stays where it was. The
job failing is the whole of what happened.

## The shape of a job

```json
{
  "jobId": 3,
  "status": "PROCESSING | FINISHED | FAILED | TIMED_OUT",
  "result":  { "stopPlaces": [ { "submittedId": "REF:StopPlace:1", "netexId": "NSR:StopPlace:1", "version": 1 } ] },
  "failure": { "reasonCode": "STALE_VERSION", "message": "...", "currentVersion": 2 }
}
```

`result` is there only on `FINISHED`. `failure` is there only on `FAILED` and `TIMED_OUT`. A
`PROCESSING` job has neither key at all — they are left out rather than sent as null, so read the
status first and the outcome second.

Inside `result`, null fields are sent. `submittedId` holds the ID you asked for and is null on an
update and on a delete, because you did not ask for one.

A successful delete also fills in `result`, with the version that terminated the stop place. A delete
makes a version like any other write.

## Running it

1. Copy the private environment file and fill it in:

   ```json
   {
     "local": {
       "clientId": "your-client-id",
       "clientSecret": "your-client-secret"
     }
   }
   ```

   Save it as `http-client.private.env.json` in this directory. It is gitignored. Never commit it.

2. Select the `local` environment in IntelliJ.

3. Run request 0 to get a token, then requests 1 to 8 in order.

Requests 2, 4, 6 and 8 read a job. If one says `PROCESSING`, run it again. That is the only reason
you have to click twice, and it is the lesson: `202` was not the end.

### Running without a token

Against a Tiamat started with `authorization.enabled=false`, skip request 0 and delete the
`Authorization: Bearer {{token}}` lines. Do not leave them in with an empty token — an
`Authorization` header that is present but unreadable answers `401` whatever the authorization
setting says.

### Pointing it somewhere else

`http-client.env.json` ships with a `local` environment only. Add others deliberately. This file
writes and then deletes a stop place, so do not point it at anything you care about.

## Getting a token

The API needs a JWT that carries a role assignment of `useWriteStopsApi` in its `role_assignments`
claim. That is the claim the API reads. The grant that produces it depends on the deployment:

| Deployment | Token URL |
| --- | --- |
| Keycloak, as in `Keycloak_Setup_Guide.md` | `{issuer}/protocol/openid-connect/token` |
| Auth0 | `{issuer}/oauth/token` |

`tokenUrl` in the environment file holds the whole URL, so switching between them is a one-line
change. Request 0 uses a client credentials grant. A deployment on a different flow needs a different
request 0, and the claim above is what that request has to produce.

## Polling

The file cannot loop. A real client must. Poll `GET /write/jobs/{jobId}` until the status is no
longer `PROCESSING`, and back off between attempts rather than polling in a tight loop. Something
like 1, 2, 4, 8 seconds, holding at a ceiling of about 30 seconds, is reasonable.

Every accepted job reaches a terminal status. A job whose worker disappears is swept to `TIMED_OUT`,
so a poll that never settles is a bug and not a state you have to design around. Still, give your
poll an upper bound, and treat hitting it as an error worth alerting on.

## When a job fails

Read `reasonCode`. Do not match on `message` — the wording can change between releases, and the codes
cannot.

| Send the same request again | Read the stop place again first | Change something first |
| --- | --- | --- |
| `TIMED_OUT`, `QUEUE_FULL` | `STALE_VERSION` | `INVALID_PAYLOAD`, `ACCESS_DENIED`, `CONSTRAINT_VIOLATION` |

`UNEXPECTED_ERROR` says nothing about what to do next, and needs a person to look at it.

`TIMED_OUT` means nothing was written, so the same request is safe to send again. `QUEUE_FULL` means
the API never took the job at all.

For `STALE_VERSION`, the failure carries `currentVersion`. Read the stop place at that version, apply
your change to what you read, and send it again. Do not simply resend with the version incremented —
somebody else's change is in there, and you would overwrite it.

A delete of a stop place that is already gone is `INVALID_PAYLOAD`, and not a `404`. The message says
the stop place is already terminated.

## What this does not cover

Multimodal stop places, the GraphQL API, and the other failure codes as runnable requests. It covers
`STALE_VERSION` because that is the one every working client has to handle.

## A warning about drift

Nothing keeps this file honest. The API can change and every test in this repository can still pass.
Requests 1 to 8 were run against a local Tiamat on 2026-09-15, and not since. Request 0 has not been
run at all, because the local Tiamat it was verified against had authorization turned off.

If this reference gets real users, running it against a test instance in a build is the obvious next
step, and the point at which it stops being able to rot.
