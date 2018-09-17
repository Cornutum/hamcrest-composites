/**
 * Provide Matchers for comparing complex Java objects with better testability.
 * 
 * <H2>What Is It?</H2>
 * 
 * <CODE>hamcrest-composites</CODE> is a collection of <A href="https://github.com/hamcrest/JavaHamcrest">Hamcrest</A> matchers for
 * comparing complex Java objects with better testability.  They apply to what might be called "composite objects", i.e. iterable
 * containers (in all their various forms) and objects whose properties define a nested tree of object references. For Java
 * programmers, <CODE>hamcrest-composites</CODE> leverages the power of Java functional interfaces to make assertions about
 * composite objects more thorough, easier to express, and easier to debug.
 * 
 * <H2>Why Composite Matchers?</H2>
 * 
 * Consider the case of a <CODE>Drawing</CODE> object that contains a collection of <CODE>Shape</CODE> instances, each of which has
 * complex properties, such as a <CODE>Color</CODE>. Consider the tests for a system that manipulates <CODE>Drawing</CODE>
 * objects. How would a test verify that a <CODE>Drawing</CODE> produced by the system contains <EM>all</EM> of the expected
 * content? With <CODE>hamcrest-composites</CODE>, it can be as simple as this:
 * 
 * <DIV style="background-color:#EEEEEE; padding:1em; margin:2em; width:90%; overflow:auto">
 * <PRE>
 * Drawing expected = ...
 * Drawing produced = ...
 * 
 * // Compare the complete tree of properties using a DrawingMatcher that extends BaseCompositeMatcher.
 * assertThat( produced, matches( new DrawingMatcher( expected)));
 * </PRE>
 * </DIV>
 * 
 * 
 * And defining a composite matcher for <CODE>Drawing</CODE> instances can be as simple as this:
 * 
 * <DIV style="background-color:#EEEEEE; padding:1em; margin:2em; width:90%; overflow:auto">
 * <PRE>
 * public class DrawingMatcher extends BaseCompositeMatcher<Drawing>
 *   {
 *   public DrawingMatcher( Drawing expected)
 *     {
 *     super( expected);
 * 
 *     // Compare values for a simple scalar property.
 *     expectThat( valueOf( "name", Drawing::getName).matches( Matchers::equalTo));
 * 
 *     // Compare values for an Iterable container property, comparing the complete tree of properties for each member.
 *     expectThat( valueOf( "elements", Drawing::getElements).matches( containsMembersMatching( ShapeMatcher::new)));
 * 
 *     // Compare values for an array property.
 *     expectThat( valueOf( "tags", Drawing::getTags).matches( Composites::containsElements));
 *     }
 *   }
 * </PRE>
 * </DIV>
 * 
 * 
 * But what if the composite match fails? For example, what if the produced <CODE>Drawing</CODE> mostly matches, except that one of
 * the shapes has the wrong color? Then you'd see an assertion error message that pinpoints the discrepancy like this:
 * 
 * <DIV style="background-color:#EEEEEE; padding:1em; margin:2em; width:90%; overflow:auto">
 * <PRE>
 * Expected: Drawing[Blues] matching elements=Iterable containing CIRCLE matching color=&lt;Color[0,0,255]&gt;
 *      but: was &lt;Color[255,0,0]&gt;
 * </PRE>
 * </DIV>
 * 
 * 
 * <H2>How Does It Work?</H2>
 * 
 * <UL>
 *   <LI><B>To add composite matchers to an assertion...</B>
 *     <UL>
 *       <LI> Use the static methods defined by the <CODE><A href="Composites.html">Composites</A></CODE> class. </LI>
 *     </UL>
 *   </LI>
 *   <P/>
 *   <LI><B>To match all properties of an object...</B>
 *     <UL>
 *       <LI> Create a subclass of <CODE><A href="BaseCompositeMatcher.html">BaseCompositeMatcher</A></CODE>. </LI>
 *       <LI> Use <CODE>expectThat()</CODE> to add to the list of matchers applied to a matched <CODE>Drawing</CODE>. </LI>
 *       <LI> Use <CODE>valueOf()</CODE> to fluently define a <CODE><A href="MatchesFunction.html">MatchesFunction</A></CODE> matcher based on a property accessor. </LI>
 *       <LI> Use methods like <CODE>containsMembersMatching()</CODE>, etc. to fluently complete the
 *            matcher for a property of type Iterable, array, or Iterator. </LI>
 *     </UL>
 *   </LI>
 *   <P/>
 *   <LI><B>To match all members of an Iterable, regardless of order...</B>
 *     <UL>
 *       <LI> Use the <CODE><A href="ContainsMembers.html">ContainsMembers</A></CODE> matcher. </LI>
 *       <LI> Even if the expected or matched Iterable may be <CODE>null</CODE>? No problem! </LI>
 *       <LI> And also compare individual members using a composite matcher? No problem! </LI>
 *     </UL>
 *   </LI>
 *   <P/>
 *   <LI><B>To match all elements of an array, regardless of order...</B>
 *     <UL>
 *       <LI> Use the <CODE><A href="ContainsElements.html">ContainsElements</A></CODE> matcher. </LI>
 *       <LI> Even if the expected or matched array may be <CODE>null</CODE>? No problem! </LI>
 *       <LI> And also compare elements using a composite matcher? No problem! </LI>
 *     </UL>
 *   </LI>
 *   <P/> <LI><B>To match all objects supplied by an Iterator, regardless of order...</B>
 *     <UL>
 *       <LI> Use the <CODE><A href="VisitsMembers.html">VisitsMembers</A></CODE> matcher. </LI>
 *       <LI> Even if the expected or matched Iterator may be <CODE>null</CODE>? No problem! </LI>
 *       <LI> And also compare individual members using a composite matcher? No problem! </LI>
 *     </UL>
 *   </LI>
 * </UL>
 * 
 * <H2>Need More Examples?</H2>
 *
 * For more examples of <CODE>hamcrest-composites</CODE> matchers in action,
 * see the <A href="https://github.com/Cornutum/hamcrest-composites/blob/masterREADME.md#need-more-examples">unit tests</A>.
 */
package org.cornutum.hamcrest;
