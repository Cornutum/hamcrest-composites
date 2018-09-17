# hamcrest-composites

## What Is It? ##

`hamcrest-composites` is a collection of [Hamcrest](https://github.com/hamcrest/JavaHamcrest) matchers for comparing complex Java objects with [better testability](#is-it-compatible-with-standard-hamcrest).
They apply to what might be called "composite objects", i.e. iterable containers (in all their various forms) and objects whose properties define a nested tree of object
references. For Java programmers, `hamcrest-composites` leverages the power of Java functional interfaces to make assertions about composite objects
more thorough, easier to express, and easier to debug.

## Why Composite Matchers? ##

Consider the case of a [`Drawing`](src/test/java/org/cornutum/hamcrest/Drawing.java) object that contains a collection of
`Shape` instances, each of which has complex properties, such as a `Color`. Consider the tests for a system that manipulates `Drawing` objects.
How would a test verify that a `Drawing` produced by the system contains *all* of the expected content? With `hamcrest-composites`, it can be
as simple as this:

```java
Drawing expected = ...
Drawing produced = ...

// Compare the complete tree of properties using a DrawingMatcher that extends BaseCompositeMatcher.
assertThat( produced, matches( new DrawingMatcher( expected)));
```

And defining a composite matcher for `Drawing` instances can be as simple as this:

```java
/**
 * A composite matcher for Drawing instances.
 */
public class DrawingMatcher extends BaseCompositeMatcher<Drawing>
  {
  public DrawingMatcher( Drawing expected)
    {
    super( expected);

    // Compare values for a simple scalar property.
    expectThat( valueOf( "name", Drawing::getName).matches( Matchers::equalTo));

    // Compare values for an Iterable container property, comparing the complete tree of properties for each member.
    expectThat( valueOf( "elements", Drawing::getElements).matches( containsMembersMatching( ShapeMatcher::new)));

    // Compare values for an array property.
    expectThat( valueOf( "tags", Drawing::getTags).matches( Composites::containsElements));
    }
  }
```

But what if the composite match fails? For example, what if the produced `Drawing` mostly matches, except that one of the shapes has the wrong color? Then you'd
see an assertion error message that pinpoints the discrepancy like this:

```
Expected: Drawing[Blues] matching elements=Iterable containing CIRCLE matching color=<Color[0,0,255]>
     but: was <Color[255,0,0]>
```
## Is It Compatible with Standard Hamcrest? ##

Yes, `hamcrest-composites` is based on standard Hamcrest 1.3. But compared to the standard Hamcrest, it offers several improvements.

* **The concept of "composite matcher" is new:** There is nothing like it in standard Hamcrest. What's new is a single Matcher class that will compare *any two* class instances
property-by-property. The problem is that every Matcher instance is bound to a specific expected value. But `BaseCompositeMatcher`, together with the `MatchesFunction`
matcher, delays binding of property value matchers using "matcher supplier" functions.

* **`ContainsMembers` vs. `IsIterableContainingInAnyOrder`:** Both of these matchers will verify that two Iterables contain the same set of members. But:
    * `ContainsMembers` works even if either the expected or the matched Iterable is `null`.
    * `ContainsMembers` accepts the members expected in multiple forms, using either an Iterable or an array or even an Iterator.
    * `ContainsMembers` can optionally apply a member-specific composite matcher to perform a "deep match" on each individual member.
    * `ContainsMembers` responds to a mismatch with a more concise and specific message, even in the case of deeply-nested collections.

* **Matchers for arrays and Iterators:** Sometimes collections come in different forms. `hamcrest-composites` provides matchers equivalent to `ContainsMembers` that
can be used to verify the contents of arrays or Iterators.

## How Does It Work? ##

* **To add composite matchers to an assertion...**
    * Use the static methods defined by the [`Composites`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/Composites.html) class.

* **To match all properties of an object...**
    * Create a subclass of [`BaseCompositeMatcher`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/BaseCompositeMatcher.html). 
    * Use `expectThat()` to add to the list of matchers applied to a matched `Drawing`. 
    * Use `valueOf()` to fluently define a [`MatchesFunction`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/MatchesFunction.html) matcher based on a property accessor. 
    * Use methods like `containsMembersMatching()`, etc. to fluently complete the matcher for a property of type Iterable, array, or Iterator. 

* **To match all members of an Iterable, regardless of order...**
    * Use the [`ContainsMembers`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/ContainsMembers.html) matcher. 
    * Even if the expected or matched Iterable may be `null`? No problem! 
    * And also compare individual members using a composite matcher? No problem! 

* **To match all elements of an array, regardless of order...**
    * Use the [`ContainsElements`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/ContainsElements.html) matcher. 
    * Even if the expected or matched array may be `null`? No problem! 
    * And also compare elements using a composite matcher? No problem! 

* **To match all objects supplied by an Iterator, regardless of order...**
    * Use the [`VisitsMembers`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/VisitsMembers.html) matcher. 
    * Even if the expected or matched Iterator may be `null`? No problem! 
    * And also compare individual members using a composite matcher? No problem! 

<H2>Need More Examples?</H2>

For full details, see the complete [Javadoc](http://www.cornutum.org/hamcrest-composites/apidocs/).

For more examples of how to use composite matchers, see the unit tests for:

* [`BaseCompositeMatcher`](src/test/java/org/cornutum/hamcrest/CompositeMatcherTest.java)
* [`ContainsElements`](src/test/java/org/cornutum/hamcrest/ContainsElementsTest.java)
* [`ContainsMembers`](src/test/java/org/cornutum/hamcrest/ContainsMembersTest.java)
* [`MatchesFunction`](src/test/java/org/cornutum/hamcrest/MatchesFunctionTest.java)
* [`VisitsMembers`](src/test/java/org/cornutum/hamcrest/VisitsMembersTest.java)

