// Set ratelimit Values
var rateLimitValues = [{
  "id": "unkown",
  "quota": "100",
  "spikeArrest": "50ps",
},
{
  "id": "others",
  "quota": "600",
  "spikeArrest": "200ps",
}];

// Create flow variable
context.setVariable("rateLimitValues", JSON.stringify(rateLimitValues));
context.setVariable("rateLimitValuesHeaderIdentifier", "et-client-name");
