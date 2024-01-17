# quarkus-byteman-junit-demo

This demo showcase how to use BMUnit5 (Byteman with Junit 5)

To use it you need to add

```
<dependency>
    <groupId>org.jboss.byteman</groupId>
    <artifactId>byteman-bmunit5</artifactId>
    <version>4.0.22</version>
</dependency>
```

to your pom.xml. There is also need to add `<argLine>-Djdk.attach.allowAttachSelf=true</argLine>` to `maven-surefire-plugin` configuration.
There is some more setting but they are not mandatory in this demo (you can see them in pom).

## Test setup

There is need to annotate the test class with `@WithByteman`.

For setting the test you can use load the rules from file or define them with annotation.
For load the file with rules use the `@BMScript(value="<your rule file>.btm")`. 

If you don't use file with rule you can define it with `@BMRule` annotation.

Optionally you can configure the BMUnit operation from test class and not setup system variables.
Example `@BMUnitConfig(loadDirectory="target/test-classes", debug=true)`

Next section describe what happening when the test is called.

## testReadVariable and testReadVariableRuleByAnnotation

The `testReadVariable` define rule by file check.btm. This file have more rules as it's loaded different tests.
Test use the rule `Change value of variable` which targeting class `GreetingResource` and method `readVariable`.
When variable response will be read the Byteman change the the value. 

The `testReadVariableRuleByAnnotation` have same rule but use `@BMRule` annotation.

## testReadVariableThrowError

The `testReadVariableThrowError` show how to throw the exception on server side by rule.
This rule is in `check.btm` with name `Throw error at exit of method`.
The rule is targeting void method `helperMethod` with no code inside.
When the method is entered the `RuntimeException` is thrown.

There should be possible to throw error inside executed method and catch it with the same method,
but this use case is little complicated and I was not been able to do it.

## testReadBindModify

The `testReadBindModify` show more complex rule.
The `readBindModify` endpoint calling `createString` method (You can see it in `GreetingResource` class).
Here is copy of the rule which should take action (`check2.btm`):
```
RULE Modify string when flag is true
CLASS GreetingResource
METHOD createString
AT READ $modify
BIND response = $response1;
IF $this.modify == true
DO traceln("This modify the response2 string");
    $response2 = "Byteman";
    $this.modified = !($this.modified);
    traceln("The modified final string will look like this: " + response + $response2);
ENDRULE
```

When method argument `modify` is accessed for read it bind the response1 variable,
evaluate if statement and do something.
You can bind any local variable or method arguments. Bind variables are only for read.
Here it's evaluating the value of global variable modify.
That's the reason why it's used `$this.modify`
(the `modify` and `this.modify` should have same value but this is just for show case)
After the successful evaluation do 4 things:
* Print to output the string `This modify the response2 string`
* Change response2 value to `Byteman`
* Change global variable `modified` to opposite value. See the bracket when evaluating.
* Print to output the string containing the bind value and modified value.

The test calling the endpoint 3 times to ensure it's all changing correctly.

# Possible issue

* All keyword must by uppercase otherwise it won't be executed.
* If you don't see the string `Installed rule using default helper : <name of your rule>`in console log your rule is probably malformed or wasn't executed.

# Links

For get more info this few links can be useful:
* https://byteman.jboss.org/
* https://github.com/bytemanproject/byteman/blob/main/docs/asciidoc/src/main/asciidoc/chapters/Byteman-Rule-Language.adoc
* https://github.com/bytemanproject/byteman/blob/main/contrib/bmunit/README.txt
