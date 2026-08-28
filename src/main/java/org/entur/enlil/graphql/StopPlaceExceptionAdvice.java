package org.entur.enlil.graphql;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.entur.enlil.stopplace.TooManyStopPlaceIdsException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Without this the exception propagates unhandled and the client sees
 * INTERNAL_ERROR with a stack trace, which is misleading for what is squarely a
 * bad request.
 */
@ControllerAdvice
public class StopPlaceExceptionAdvice {

  @GraphQlExceptionHandler
  public GraphQLError handle(
    TooManyStopPlaceIdsException ex,
    DataFetchingEnvironment environment
  ) {
    return GraphqlErrorBuilder
      .newError(environment)
      .errorType(ErrorType.BAD_REQUEST)
      .message(ex.getMessage())
      .build();
  }
}
