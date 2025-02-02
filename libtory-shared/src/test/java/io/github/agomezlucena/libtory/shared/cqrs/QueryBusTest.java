package io.github.agomezlucena.libtory.shared.cqrs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QueryBusTest {
    @Test
    @DisplayName("should allow to add a new QueryHandler")
    void shouldAllowToAddNewQueryHandler() {
        var testSubject = new QueryBus();
        QueryHandler<PagedQuery<TestEntity>, List<TestEntity>> mockedHandler = mock();
        assertDoesNotThrow(()-> testSubject.addHandler(TestEntityPagedQuery.class,mockedHandler));
    }

    @Test
    @DisplayName("should return the same object after adding a query handler")
    void shouldReturnTheSameObjectAfterAddingQueryHandler() {
        var testSubject = new QueryBus();
        QueryHandler<PagedQuery<TestEntity>, List<TestEntity>> mockedHandler = mock();
        var result = assertDoesNotThrow(()-> testSubject.addHandler(TestEntityPagedQuery.class,mockedHandler));
        assertSame(testSubject, result);
    }

    @Test
    @DisplayName("should call to the expected query handler")
    void shouldCallToTheExpectedQueryHandler() {
        var testSubject = new QueryBus();
        var expectedValue = List.of(new TestEntity(1,"test"));

        QueryHandler<PagedQuery<TestEntity>, List<TestEntity>> mockedHandler = mock();
        testSubject.addHandler(TestEntityPagedQuery.class,mockedHandler);
        when(mockedHandler.handle(any())).thenReturn(expectedValue);

        var obtainedValue = testSubject.handle(new TestEntityPagedQuery(0,1,null,null));
        assertEquals(expectedValue, obtainedValue);
    }

    @Test
    @DisplayName("should throw when then query bus doesn't have a handler for the given query")
    void shouldThrowWhenThenQueryBusDoesNotHaveAHandlerForTheGivenQuery() {
        var testSubject = new QueryBus();
        var expectedErrorMessage = "the query is not supported in this bus";
        var obtainedException = assertThrows(CqrsError.class, ()-> testSubject.handle(mock(PagedQuery.class)))
                .getMessage();
        assertEquals(expectedErrorMessage,obtainedException);
    }

    private record TestEntity(int id, String name) {}

    private static class TestEntityPagedQuery extends AbstractPagedQuery<TestEntity> {
        public TestEntityPagedQuery(Integer page, Integer size, String sortingField, String sortingDirection) {
            super(page, size, sortingField, sortingDirection);
        }

        @Override
        public boolean sortingFieldIsInvalid(String ignored) {
            return false;
        }
    }
}