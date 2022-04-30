package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.zooseeker.models.Animal;
import com.example.zooseeker.models.AnimalItemDao;
import com.example.zooseeker.repositories.AnimalDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private AnimalItemDao dao;
    private AnimalDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AnimalDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.animalItemDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsert() {
        Animal animal1 = new Animal("Lion", "lion");
        Animal animal2 = new Animal("Tiger", "tiger");

        long id1 = dao.insert(animal1);
        long id2 = dao.insert(animal2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testGet() {
        Animal insertedItem = new Animal("Lion", "lion");

        dao.insert(insertedItem);
        List<Animal> item = dao.get("lion");

        assertEquals("lion", item.get(0).id);
        assertEquals("Lion", item.get(0).name);
    }

    @Test
    public void testUpdate() {
        Animal insertedItem = new Animal("Lion", "lion");
        dao.insert(insertedItem);

        List<Animal> item = dao.get("lion");
        item.get(0).name = "LionKing";
        int itemsUpdated = dao.update(item.get(0));
        assertEquals(1, itemsUpdated);

        item = dao.get("lion");
        assertNotNull(item);
        assertEquals("LionKing", item.get(0).name);
    }

    @Test
    public void testDelete() {
        Animal insertedItem = new Animal("Lion", "lion");
        dao.insert(insertedItem);

        List<Animal> item = dao.get("lion");
        int itemsDeleted = dao.delete(item.get(0));
        assertEquals(1, itemsDeleted);
        assertEquals(0, dao.get("lion").size());
    }
}
