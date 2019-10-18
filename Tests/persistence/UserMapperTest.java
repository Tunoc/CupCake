package persistence;

import logic.Role;
import logic.User;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserMapperTest {
    @InjectMocks
    private UserMapper mapper;

    @Mock
    PreparedStatement ps;
    @Mock
    ResultSet resSet;
    @Mock
    SQLConnection connection;
    @Mock
    Connection sqlConnection;

    @Before
    public void setup() throws Exception {
    }

    private static int i = 0;
    public boolean mockNext() {
        if(i == 0){
            i++;
            return true;
        }
        else {
            i++;
            return false;
        }
    }

    @Test
    public void test_getAllUsers() throws SQLException {
        // First and second time next() is called, it returns true, then it returns false.
        when(resSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resSet.getInt("user_id")).thenReturn(1);
        when(resSet.getString("user_name")).thenReturn("Peter Larsen");
        when(resSet.getString("user_role")).thenReturn("CUSTOMER");
        when(resSet.getString("login_mail")).thenReturn("peter@example.com");
        when(resSet.getInt("account_id")).thenReturn(1);
        when(resSet.getInt("user_balance")).thenReturn(1000);

        when(connection.getConnection()).thenReturn(sqlConnection);
        when(sqlConnection.prepareStatement(any(String.class))).thenReturn(ps);
        when(connection.selectQuery(ps)).thenReturn(resSet);

        ArrayList<User> users = mapper.getAllUser();
        User user = users.get(0);
        assertEquals(1,user.getID());
        assertEquals("Peter Larsen",user.getName());
        assertEquals(Role.CUSTOMER, user.getRole());
        assertEquals("peter@example.com",user.getMail());
        assertEquals(1,user.getAccount().getId());
        assertEquals(1000,user.getAccount().getBalance());
        assertEquals(2, users.size());
    }
}
