package de.mobcom.group3.gotrack;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import java.util.List;

import de.mobcom.group3.gotrack.Database.DAO.DbHelper;
import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = JELLY_BEAN, packageName = "de.mobcom.group3.gotrack")
public class DatabaseUnitTest {
    private RouteDAO routeDAO;
    private UserDAO userDAO;
    private DbHelper dbHelper;
    private int id;

/*--------------------------------------------------------------------------------------------------
                                    general test configurations
 -------------------------------------------------------------------------------------------------*/
    @Before
    public void setup() {
        dbHelper = new DbHelper(RuntimeEnvironment.application);
        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 2);
        userDAO = new UserDAO(RuntimeEnvironment.application);
        //routeDAO = new RouteDAO(RuntimeEnvironment.application);
    }

    @After
    public void cleanup() {
        dbHelper.onDowngrade(dbHelper.getWritableDatabase(), 2, 1);
        dbHelper.close();
    }

/*--------------------------------------------------------------------------------------------------
                                    user model and userDAO tests
 -------------------------------------------------------------------------------------------------*/

    // create user with empty constructor
    // write to database, read from database, assert attributes and delete from database
    @Test
    public void insertUserEmptyConstructor() {
        User createdUser = new User();
        createdUser.setFirstName("Manuel");
        createdUser.setLastName("Neuer");
        createdUser.setActive(true);
        createdUser.setDarkThemeActive(true);
        createdUser.setHintsActive(false);
        createdUser.setMail("manuel.neuer@mail.org");
        createdUser.setImage(null);
        userDAO.create(createdUser);
        id = createdUser.getId();
        User userFromDbRead = userDAO.read(createdUser.getId());
        Assert.assertEquals(createdUser.getFirstName(), userFromDbRead.getFirstName());
        Assert.assertEquals(createdUser.getLastName(), userFromDbRead.getLastName());
        Assert.assertEquals(createdUser.getMail(), userFromDbRead.getMail());
        Assert.assertEquals(id, userFromDbRead.getId());
        Assert.assertEquals(createdUser.getImage(), userFromDbRead.getImage());
        userDAO.delete(createdUser);
    }

    // create user with ui constructor
    // write to database, read from database, assert attributes and delete from database
    @Test
    public void insertUserUiConstructor() {
        User createdUser = new User("Manuel", "Neuer", "manuel.neuer@mail.org", null);
        createdUser.setActive(true);
        createdUser.setDarkThemeActive(true);
        createdUser.setHintsActive(false);
        userDAO.create(createdUser);
        User userFromDbRead = userDAO.read(createdUser.getId());
        Assert.assertEquals(createdUser.getFirstName(), userFromDbRead.getFirstName());
        Assert.assertEquals(createdUser.getLastName(), userFromDbRead.getLastName());
        Assert.assertEquals(createdUser.getMail(), userFromDbRead.getMail());
        Assert.assertEquals(createdUser.getId(), userFromDbRead.getId());
        Assert.assertEquals(createdUser.getImage(), userFromDbRead.getImage());
        userDAO.delete(createdUser);
    }

    // create user with database constructor
    // write to database, read from database, assert attributes and delete from database
    @Test
    public void insertUserDbConstructor() {
        User createdUser = new User(
                5,
                "Manuel",
                "Neuer",
                1,
                1,
                0,
                "manuel.neuer@mail.org",
                null);
        userDAO.create(createdUser);
        User userFromDbRead = userDAO.read(createdUser.getId());
        Assert.assertEquals(createdUser.getFirstName(), userFromDbRead.getFirstName());
        Assert.assertEquals(createdUser.getLastName(), userFromDbRead.getLastName());
        Assert.assertEquals(createdUser.getMail(), userFromDbRead.getMail());
        Assert.assertEquals(createdUser.getId(), userFromDbRead.getId());
        Assert.assertEquals(createdUser.getImage(), userFromDbRead.getImage());
        userDAO.delete(createdUser);
    }

    // create two user with empty constructor
    // write to database, read from database, assert attributes and delete from database
    @Test
    public void readAllUser() {
        User user1 = new User();
        user1.setFirstName("Manuel");
        user1.setLastName("Neuer");
        user1.setActive(true);
        user1.setDarkThemeActive(true);
        user1.setHintsActive(false);
        user1.setMail("manuel.neuer@mail.org");
        user1.setImage(null);
        userDAO.create(user1);
        User user2 = new User();
        user2.setFirstName("Andreas");
        user2.setLastName("Schulte");
        user2.setActive(false);
        user2.setDarkThemeActive(false);
        user2.setHintsActive(true);
        user2.setMail("andreas.schulte@hotmail.com");
        user2.setImage(null);
        userDAO.create(user2);
        List<User> userList = userDAO.readAll();
        Assert.assertEquals(userList.get(0).getFirstName(), user2.getFirstName());
        Assert.assertEquals(userList.get(0).getLastName(), user2.getLastName());
        Assert.assertEquals(userList.get(0).getId(), user2.getId());
        Assert.assertEquals(userList.get(0).getMail(), user2.getMail());
        Assert.assertEquals(userList.get(0).getImage(), user2.getImage());
        Assert.assertEquals(userList.get(1).getFirstName(), user1.getFirstName());
        Assert.assertEquals(userList.get(1).getLastName(), user1.getLastName());
        Assert.assertEquals(userList.get(1).getId(), user1.getId());
        Assert.assertEquals(userList.get(1).getMail(), user1.getMail());
        Assert.assertEquals(userList.get(1).getImage(), user1.getImage());
        userDAO.delete(user1);
        userDAO.delete(user2);
    }

    // create user with empty constructor
    // write to database, alter attributes, update in database,
    // read from database, assert attributes and delete from database
    @Test
    public void updateUser() {
        User createdUser = new User();
        createdUser.setFirstName("Manuel");
        createdUser.setLastName("Neuer");
        createdUser.setActive(true);
        createdUser.setDarkThemeActive(true);
        createdUser.setHintsActive(false);
        createdUser.setMail("manuel.neuer@mail.org");
        createdUser.setImage(null);
        userDAO.create(createdUser);
        createdUser.setFirstName("Andreas");
        createdUser.setLastName("Schulte");
        createdUser.setActive(false);
        createdUser.setDarkThemeActive(false);
        createdUser.setHintsActive(true);
        createdUser.setMail("andreas.schulte@hotmail.com");
        userDAO.update(createdUser.getId(), createdUser);
        User userFromDbUpdate = userDAO.read(createdUser.getId());
        Assert.assertEquals(createdUser.getFirstName(), userFromDbUpdate.getFirstName());
        Assert.assertEquals(createdUser.getLastName(), userFromDbUpdate.getLastName());
        Assert.assertEquals(createdUser.getMail(), userFromDbUpdate.getMail());
        Assert.assertEquals(createdUser.getId(), userFromDbUpdate.getId());
        Assert.assertEquals(createdUser.getImage(), userFromDbUpdate.getImage());
        userDAO.delete(createdUser);
    }


/*--------------------------------------------------------------------------------------------------
                                    route model and routeDAO tests
 -------------------------------------------------------------------------------------------------*/




}
