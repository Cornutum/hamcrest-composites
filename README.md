# hamcrest-composites

## What Is It? ##

`hamcrest-composites` is a collection of [Hamcrest](https://github.com/hamcrest/JavaHamcrest) matchers for comparing complex Java objects with better testability.
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
    expectThat( valueOf( "elements", Drawing::getElements).matches( elements -> containsMembers( ShapeMatcher::new, elements)));

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

## How Does It Work? ##

## What's the API? ##



