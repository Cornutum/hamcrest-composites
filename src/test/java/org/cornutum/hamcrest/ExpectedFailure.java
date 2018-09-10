//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import static org.junit.Assert.fail;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Reports a failure if an expected Throwable is not thrown by a given action.
 */
public class ExpectedFailure<T extends Throwable>
  {
  private final Class<T> failureType;
  private Optional<T> expected;
  
  /**
   * Creates a new ExpectedFailure instance.
   */
  public ExpectedFailure( Class<T> failureType)
    {
    this.failureType = failureType;
    }

  /**
   * Throws an AssertionError if the expected Throwable is not thrown by the given action.
   */
  @SuppressWarnings("unchecked")
  public ExpectedFailure<T> when( Runnable action)
    {
    try
      {
      expected = Optional.empty();
      action.run();
      }
    catch( Throwable t)
      {
      if( !failureType.isInstance( t))
        {
        fail( "Unexpected failure: " + t);
        }
      else
        {
        expected = Optional.of( (T) t);
        }
      }

    if( !expected.isPresent())
      {
      fail( "Expected " + failureType.getSimpleName() + " was not thrown");
      }

    return this;
    }

  /**
   * After {@link #when performing an action}, apply the given checker to verify details of the expected failure.
   */
  public void then( Consumer<T> failureChecker)
    {
    if( expected == null)
      {
      throw new IllegalStateException( "No action performed to produce the expected failure");
      }

    expected.ifPresent( failureChecker::accept);
    }

  /**
   * Returns a new ExpectedFailure instance.
   */
  public static <F extends Throwable> ExpectedFailure<F> expectFailure( Class<F> failureType)
    {
    return new ExpectedFailure<F>( failureType);
    }

  /**
   * Returns a new ExpectedFailure instance that expects an AssertionError.
   */
  public static ExpectedFailure<AssertionError> expectFailure()
    {
    return new ExpectedFailure<AssertionError>( AssertionError.class);
    }
  }
