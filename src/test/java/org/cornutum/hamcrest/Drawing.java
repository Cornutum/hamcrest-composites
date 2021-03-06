//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.hamcrest.Matchers;

public class Drawing
  {
  private final String name;
  private final List<Shape> elements;
  private String[] tags;

  public Drawing( String name, Shape... elements)
    {
    this.name = name;
    this.elements = Arrays.asList( elements);
    }

  public String getName()
    {
    return name;
    }

  public List<Shape> getElements()
    {
    return elements;
    }

  public void setTags( String... tags)
    {
    this.tags = tags;
    }

  public String[] getTags()
    {
    return tags;
    }
  
  public String toString()
    {
    return "Drawing[" + getName() + "]";
    }

  public boolean equals( Object object)
    {
    Drawing other =
      object != null && object.getClass().equals( getClass())
      ? (Drawing) object
      : null;

    return
      other != null
      && Objects.equals( other.getName(), getName());
    }

  public int hashCode()
    {
    return
      getClass().hashCode()
      ^ Objects.hashCode( getName());
    }

  public static Shape circle( Color color)
    {
    return new Shape( Shape.Type.CIRCLE, color);
    }

  public static Shape rectangle( Color color)
    {
    return new Shape( Shape.Type.RECTANGLE, color);
    }

  public static Shape triangle( Color color)
    {
    return new Shape( Shape.Type.TRIANGLE, color);
    }
  
  public static class Color
    {
    private final int red;
    private final int green;
    private final int blue;

    public static final Color RED = new Color( 255, 0, 0);
    public static final Color GREEN = new Color( 0, 255, 0);
    public static final Color BLUE = new Color( 0, 0, 255);
    
    public Color( int red, int green, int blue)
      {
      this.red = red;
      this.green = green;
      this.blue = blue;
      }

    public int getRed()
      {
      return red;
      }

    public int getGreen()
      {
      return green;
      }

    public int getBlue()
      {
      return blue;
      }

    public String toString()
      {
      return "Color[" + getRed() + "," + getGreen() + "," + getBlue() + "]";
      }

    public boolean equals( Object object)
      {
      Color other =
        object != null && object.getClass().equals( getClass())
        ? (Color) object
        : null;

      return
        other != null
        && other.getRed() == getRed()
        && other.getGreen() == getGreen()
        && other.getBlue() == getBlue();
      }

    public int hashCode()
      {
      return
        getClass().hashCode()
        ^ getRed()
        ^ getGreen()
        ^ getBlue();
      }
    }
  
  public static class Shape
    {
    private final Type type;
    private final Color color;

    public enum Type
      {
        CIRCLE,
        RECTANGLE,
        TRIANGLE
      };
    
    public Shape( Type type, Color color)
      {
      this.type = type;
      this.color = color;
      }

    public Type getType()
      {
      return type;
      }

    public Color getColor()
      {
      return color;
      }

    public String toString()
      {
      return String.valueOf( getType()) + "[" + getColor() + "]";
      }

    public boolean equals( Object object)
      {
      Shape other =
        object != null && object.getClass().equals( getClass())
        ? (Shape) object
        : null;

      return
        other != null
        && Objects.equals( other.getType(), getType());
      }

    public int hashCode()
      {
      return
        getClass().hashCode()
        ^ Objects.hashCode( getType());
      }
    }

  /**
   * A composite matcher for Drawing instances.
   */
  public static class DrawingMatcher extends BaseCompositeMatcher<Drawing>
    {
    /**
     * Creates a new DrawingMatcher instance.
     */
    public DrawingMatcher( Drawing expected)
      {
      super( expected);
      expectThat( valueOf( "name", Drawing::getName).matches( Matchers::equalTo));
      expectThat( valueOf( "elements", Drawing::getElements).matches( containsMembersMatching( ShapeMatcher::new)));
      expectThat( valueOf( "tags", Drawing::getTags).matches( Composites::containsElements));
      }
    }

  /**
   * A composite matcher for Shape instances.
   */
  public static class ShapeMatcher extends BaseCompositeMatcher<Shape>
    {
    /**
     * Creates a new ShapeMatcher instance.
     */
    public ShapeMatcher( Shape expected)
      {
      super( expected);
      expectThat( "type", Shape::getType, Matchers::equalTo);
      expectThat( "color", Shape::getColor, Matchers::equalTo);
      }
    }
  }

