package io.github.agomezlucena.libtory.shared.cqrs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("A command bus should")
class CommandBusTest {
    @Test
    @DisplayName("not allow to add an invalid handler")
    void shouldNotAllowToAddInvalidHandler() {
        CommandHandler<FakeCommand> givenHandler = mock();
        var testSubject = CommandBus.getNewCommandBus();
        var expectedMessage =  String.format(
                "handler: %s can not handle command: Integer",
                givenHandler.getClass().getSimpleName()
        );
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> testSubject.addHandler(Integer.class,givenHandler)
        );

        verify(givenHandler).canHandle(Integer.class);
        assertEquals(expectedMessage,exception.getMessage());
    }

    @Test
    @DisplayName("fail when don't find a handler for a command")
    void shouldFailWhenDoNotFindAHandlerForACommand(){
        var testSubject = CommandBus.getNewCommandBus();
        var expectedMessage = "not handler found for command: Integer";
        var obtainedException = assertThrows(
                IllegalArgumentException.class,
                ()-> testSubject.sendCommand(1)
        );
        assertEquals(expectedMessage,obtainedException.getMessage());
    }

    @Test
    @DisplayName("allow to add handler that can manage the given command class and return the same command handler")
    void shouldAllowToAddAHandlerThatCanManageTheGivenCommandClassAndReturnTheSameCommandHandler(){
        var testSubject = CommandBus.getNewCommandBus();
        CommandHandler<FakeCommand> mockedHandler = mock();
        when(mockedHandler.canHandle(FakeCommand.class)).thenReturn(true);

        var result = assertDoesNotThrow(()->testSubject.addHandler(FakeCommand.class, mockedHandler));

        assertNotNull(result);
        verify(mockedHandler).canHandle(FakeCommand.class);
    }

    private record FakeCommand(){

    }
}