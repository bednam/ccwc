# solution to #1 [coding challange](https://codingchallenges.fyi)

Command line wc tool implemented using Scala Native with cats-effect, fs2 and decline. 

Tested with munit with a little help of os-lib.

Set up to be compiled, packaged and tested using [scala-cli](https://scala-cli.virtuslab.org).

---

## Package
### creates native executable <b>ccwc</b>
<p><code>scala-cli --power package -f CCWC.scala</code></p>

## Test
### requires executable created in the <b>Package</b> step
<p><code>scala-cli test CCWC.test.scala</code></p>

---
Run native executable <code>./ccwc --help</code> to display usage instructions.

Supports reading from file or standard input:

<p><code>./ccwc --lines=pg132.txt</code></p>
<p><code>cat pg132.txt | ./ccwc --lines</code></p>
