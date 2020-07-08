# hamcrest-composites

## What's New? ##

  * The latest version ([1.2.0](https://github.com/Cornutum/hamcrest-composites/releases/tag/release-1.2.0))
    is now available at the [Maven Central Repository](https://search.maven.org/search?q=hamcrest-composites).

## What Is It? ##

`hamcrest-composites` is a collection of [Hamcrest](https://github.com/hamcrest/JavaHamcrest) matchers for comparing complex Java objects with [better testability](#is-it-compatible-with-standard-hamcrest).
They apply to what might be called "composite objects", i.e. iterable containers (in all their various forms) and objects whose properties define a nested tree of object
references. For Java programmers, `hamcrest-composites` leverages the power of Java functional interfaces to make assertions about composite objects
more thorough, easier to express, and easier to debug.

## Why Composite Matchers? ##

With standard Hamcrest, verifying that two objects are `equals` is easy. But comparing the full tree of object property values is much more involved and not directly supported. Although such "deep matching" is needed for testing, it's often impossible (and almost always wrong!) to implement it using `equals`. Instead, `hamcrest-composites` makes it much easier to implement deep matching using a "composite matcher". Similarly, because standard Hamcrest has always been a bit weak for comparing collections and arrays, `hamcrest-composites` adds more robust matchers for all types of iterable containers.

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

* **`ContainsMembers` vs. `IsIterableContainingInAnyOrder`:** Both of these matchers will verify that two Iterables contain the same set of members.
Likewise, `ListsMembers` is similar to `IsIterableContainingInOrder`. But:
    * `ContainsMembers` and `ListMembers` work even if either the expected or the matched Iterable is `null`.
    * `ContainsMembers` and `ListMembers` accept the members expected in multiple forms, using either an Iterable or an array or even an Iterator.
    * `ContainsMembers` and `ListMembers` can optionally apply a member-specific composite matcher to perform a "deep match" on each individual member.
    * `ContainsMembers` and `ListMembers` respond to a mismatch with a more concise and specific message, even in the case of deeply-nested collections.

* **Matchers for arrays and Iterators:** Sometimes collections come in different forms. `hamcrest-composites` provides matchers equivalent to `ContainsMembers` and `ListMembers` that
can be used to verify the contents of arrays or Iterators.

## How Does It Work? ##

* **To add composite matchers to an assertion...**
    * Use the static methods defined by the [`Composites`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/Composites.html) class.

* **To match all properties of an object...**
    * Create a subclass of [`BaseCompositeMatcher`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/BaseCompositeMatcher.html). 
    * Use `expectThat()` to add to the list of matchers applied to a matched object. 
    * Use `valueOf()` to fluently define a [`MatchesFunction`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/MatchesFunction.html) matcher based on a property accessor. 
    * Use methods like `containsMembersMatching()`, etc. to fluently complete the matcher for a property of type Iterable, array, or Iterator. 

* **To match all members of an iterable container, regardless of order...**
    * To match an Iterable, use the [`ContainsMembers`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/ContainsMembers.html) matcher. 
    * To match an array, use the [`ContainsElements`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/ContainsElements.html) matcher. 
    * To match an Iterator, use the [`VisitsMembers`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/VisitsMembers.html) matcher. 
    * Even if the expected or matched container may be `null`? No problem! 
    * And also compare individual members using a composite matcher? No problem! 

* **To match all members of a sequence, in order...**
    * To match an Iterable, use the [`ListsMembers`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/ListsMembers.html) matcher. 
    * To match an array, use the [`ListsElements`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/ListsElements.html) matcher. 
    * To match an Iterator, use the [`VisitsList`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/VisitsList.html) matcher. 
    * Without using `equals()`, use the [`ListsMatching`](http://www.cornutum.org/hamcrest-composites/apidocs/org/cornutum/hamcrest/ListsMatching.html) matcher. 
    * Even if the expected or matched sequence may be `null`? No problem! 
    * And also compare individual members using a composite matcher? No problem! 

<H2>Need More Examples?</H2>

For full details, see the complete [Javadoc](http://www.cornutum.org/hamcrest-composites/apidocs/).

For more examples of how to use composite matchers, see the unit tests for:

* [`BaseCompositeMatcher`](src/test/java/org/cornutum/hamcrest/CompositeMatcherTest.java)
* [`ContainsElements`](src/test/java/org/cornutum/hamcrest/ContainsElementsTest.java)
* [`ContainsMembers`](src/test/java/org/cornutum/hamcrest/ContainsMembersTest.java)
* [`ListsElements`](src/test/java/org/cornutum/hamcrest/ListsElementsTest.java)
* [`ListsMembers`](src/test/java/org/cornutum/hamcrest/ListsMembersTest.java)
* [`ListsMatching`](src/test/java/org/cornutum/hamcrest/ListsMatchingTest.java)
* [`MatchesFunction`](src/test/java/org/cornutum/hamcrest/MatchesFunctionTest.java)
* [`VisitsList`](src/test/java/org/cornutum/hamcrest/VisitsListTest.java)
* [`VisitsMembers`](src/test/java/org/cornutum/hamcrest/VisitsMembersTest.java)
