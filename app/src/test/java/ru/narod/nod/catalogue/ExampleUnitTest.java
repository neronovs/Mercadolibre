package ru.narod.nod.catalogue;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import ru.narod.nod.catalogue.Model.Model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {

    // mock creation
    List mockedList = mock(List.class);

    @Test
    public void substr() {
        assertEquals("ing", "string".substring(3));
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    Context context = Mockito.mock(Context.class);

    @Test
    public void modelIsOnline() {
        assertTrue(Model.isOnline(context));
    }
}