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
        Animal animal1 = new Animal("Lion", "North");
        Animal animal2 = new Animal("Tiger", "South");

        long id1 = dao.insert(animal1);
        long id2 = dao.insert(animal2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testGet() {
        Animal insertedItem = new Animal("Lion", "North");
        long id = dao.insert(insertedItem);

        Animal item = dao.get(id);
        assertEquals(id, item.id);
        assertEquals(insertedItem.name, item.name);
        assertEquals(insertedItem.location,item.location);
    }

    @Test
    public void testUpdate() {
        Animal item = new Animal("Lion", "North");
        long id = dao.insert(item);

        item = dao.get(id);
        item.location = "East";
        int itemsUpdated = dao.update(item);
        assertEquals(1, itemsUpdated);

        item = dao.get(id);
        assertNotNull(item);
        assertEquals("East", item.location);
    }

    @Test
    public void testDelete() {
        Animal item = new Animal("Lion", "North");
        long id = dao.insert(item);

        item = dao.get(id);
        int itemsDeleted = dao.delete(item);
        assertEquals(1, itemsDeleted);
        assertNull(dao.get(id));
    }
}
