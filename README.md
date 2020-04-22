## Code Style

The Java code must use the Google formatting guidelines. Format is checked using
[google-java-format](https://github.com/google/google-java-format).

The formatting is checked in the tests and formatting errors will cause tests
to fail and comments to be added to your PR.

You can ensure your files are formatted correctly either by installing
google-java-format into your editor, or by running `mvn spotless:check`.
To fix formatting issues run: `mvn spotless:apply`

## Static Analysis

Code is checked for common flaws with [PMD](https://pmd.github.io). It can be
executed by running `mvn pmd:pmd pmd:check`.