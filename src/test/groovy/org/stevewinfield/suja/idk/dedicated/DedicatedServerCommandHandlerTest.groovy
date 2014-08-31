package org.stevewinfield.suja.idk.dedicated

import org.junit.Before
import org.junit.Test
import org.stevewinfield.suja.idk.dedicated.commands.DedicatedServerCommandHandler
import org.stevewinfield.suja.idk.dedicated.commands.IDedicatedServerCommand

class DedicatedServerCommandHandlerTest {
    DedicatedServerCommandHandler handler;

    @Before
    void setUp() {
        handler = new DedicatedServerCommandHandler()
    }

    @Test
    void testRegisterCommand() {
        handler.registerCommand([
                'getName': { 'test' },
                'execute': { args, logger -> }
        ] as IDedicatedServerCommand)
        assert handler.getCommand('test') != null
    }

    @Test
    void testHandleCommandWithoutArguments() {
        def executed = false
        handler.registerCommand([
                'getName': { 'test' },
                'execute': { args, logger ->
                    executed = true
                }
        ] as IDedicatedServerCommand)
        handler.handle('test')
        assert executed
    }

    @Test
    void testHandleCommandWithArguments() {
        def executed = false
        handler.registerCommand([
                'getName': { 'test' },
                'execute': { args, logger ->
                    executed = true
                    assert args.length == 2
                    assert args[0] == 'me'
                    assert args[1] == 'not'
                }
        ] as IDedicatedServerCommand)
        handler.handle('test me not')
        assert executed
    }

    @Test
    void testHandleCommandNotFound() {
        def executed = false
        handler.registerCommand([
                'getName': { 'test' },
                'execute': { string, logger ->
                    executed = true
                }
        ] as IDedicatedServerCommand)
        handler.handle('nonexistingcommand')
        assert !executed
    }
}

