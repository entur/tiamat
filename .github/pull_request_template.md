## PR Instructions

When creating a pull request, please follow the format below. For each section, *replace* the
guidance text with your own text, keeping the section heading. If you have nothing to say in a
particular section, you can completely delete the section including its heading to indicate that you
have taken the requested steps. None of these instructions or the guidance text (non-heading text)
should be present in the submitted PR. These sections serve as a checklist: when you have replaced
or deleted all of them, the PR is considered complete.

### Summary

Please include a summary of the changes and the related issue(s). Ensure you explain the purpose and context clearly.

> **Example:**
> - Adds support for FareZones in NeTEx import
> - Fixes bug when parsing stop places with empty names (#123)

### Type of change
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update (changes to documentation only)
- [ ] Other (please describe)

### Issue

Link to or create an [issue](https://github.com/entur/tiamat/issues) that
describes the relevant feature or bug. You need not create an issue for small bugfixes and code
cleanups, but in that case do describe the problem clearly and completely in the "summary" section
above. In the linked issue (or summary section for smaller PRs) please describe:

- Motivation (problem or need encountered)
- How the code works
- Technical approach and any design considerations or decisions

Remember that the PR will be reviewed by another developer who may not be familiar with your use
cases or the code you're modifying. It generally takes much less effort for the author of a PR to
explain the background and technical details than for a reviewer to infer or deduce them. PRs may be
closed if they or their linked issues do not contain sufficient information for a reviewer to
proceed.

Add [GitHub keywords](https://help.github.com/articles/closing-issues-using-keywords/) to this PR's
description, for example:

Closes #45

### Unit tests

Write a few words on how the new code is tested.

- Were unit tests added/updated?
- Was any manual verification done?
- Any observations on changes to performance?
- Was the code designed so it is unit testable?
- Were any tests applied to the smallest appropriate unit?
- Do all tests
  pass [the continuous integration service](https://github.com/entur/tiamat/actions)?

### Documentation

- Have you added documentation in code covering design and rationale behind the code?
- Were all non-trivial public classes and methods documented with Javadoc?
---
<!-- Thank you for your contribution to entur/tiamat! -->


