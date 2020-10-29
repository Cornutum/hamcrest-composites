//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
  public ExpectedFailure<T> when( Failable action)
    {
    return when( action, Function.identity());
    }

  /**
   * Throws an AssertionError if the expected Throwable is not thrown by the given action.
   * The expected Throwable is defined by applying the <CODE>failureMapper</CODE> to the
   * Throwable produced by the action.
   */
  @SuppressWarnings("unchecked")
  public ExpectedFailure<T> when( Failable action, Function<Throwable,Throwable> failureMapper)
    {
    expected = Optional.empty();

    Throwable failure = action.get().map( t -> failureMapper.apply(t)).orElse( null);
    if( failure == null)
      {
      throw new AssertionError( "Expected " + failureType.getSimpleName() + " was not thrown");
      }
    if( !failureType.isInstance( failure))
      {
      throw new AssertionError( "Unexpected failure: " + failure);
      }

    expected = Optional.of( (T) failure);
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
    return expectFailure( AssertionError.class);
    }

  public String toString()
    {
    return String.format( "%s[%s]", getClass().getSimpleName(), failureType.getSimpleName());
    }

  /**
   * Implements an action that could throw any type of Throwable, including a checked exception.
   */
  @FunctionalInterface
  public interface Failable extends Supplier<Optional<Throwable>>
    {
    @Override
    default Optional<Throwable> get()
      {
      Optional<Throwable> failure;
      try
        {
        run();
        failure = Optional.empty();
        }
      catch( Throwable e)
        {
        failure = Optional.of( e);
        }

      return failure;
      }

    void run() throws Throwable;
    }
  }
