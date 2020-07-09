//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2020, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import org.hamcrest.Matcher;
import org.hamcrest.core.IsInstanceOf;

/**
 * Base class for a composite matcher the verifies the expected type of a matched object.
 */
public abstract class ClassCompositeMatcher<T> extends BaseCompositeMatcher<T>
  {
  /**
   * Creates a new ClassCompositeMatcher instance.
   */
  protected ClassCompositeMatcher( Class<? extends T> expectedType, T expected)
    {
    super( expected);
    setExpectedType( expectedType);
    }

  /**
   * Changes the expected type for this matcher.
   */
  protected void setExpectedType( Class<? extends T> expectedType)
    {
    this.expectedType = expectedType;
    }

  /**
   * Returns the expected type for this matcher.
   */
  protected Class<? extends T> getExpectedType()
    {
    return expectedType;
    }

  /**
   * If the given object is incompatible with the expected type, returns a Matcher that describes the mismatch.
   * Otherwise, return null;
   */
  protected Matcher<? super T> getTypeMismatch( Object object)
    {
    return
      object != null && getExpectedType().isAssignableFrom( object.getClass())
      ? null
      : new IsInstanceOf( getExpectedType());
    }

  public String toString()
    {
    return String.format( "%s[%s]", getClass().getSimpleName(), getExpectedType().getSimpleName());
    }
  
  private Class<? extends T> expectedType;
  }
