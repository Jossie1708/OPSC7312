import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frogstore.droneapp.Activities.ImageViewerActivity
import com.frogstore.droneapp.Activities.LocationActivity
import com.frogstore.droneapp.Activities.MainActivity
import com.frogstore.droneapp.SideMenuNavBarActivity
import com.frogstore.droneapp.Adapters.NotificationsAdapter
import com.frogstore.droneapp.ImageAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class LocationActivityTest {
    private lateinit var activity: LocationActivity

    @Before
    fun setup() {
        activity = LocationActivity()
    }

    @Test
    fun testOnCreate() {
        // Arrange
        val savedInstanceState: Bundle = mock() // Using mock<Bundle>() is also valid.

        // Act
        activity.onCreate(savedInstanceState)

        // Assert
        // Verify that the activity was created successfully (Add meaningful assertions)
    }
}

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var activity: MainActivity

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var preferenceManager: PreferenceManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        activity = MainActivity()
    }

    @Test
    fun testOnCreate() {
        // Arrange
        val savedInstanceState: Bundle = mock()

        // Act
        activity.onCreate(savedInstanceState)

        // Assert
        verify(PreferenceManager.getDefaultSharedPreferences(activity)).also {
            verify(sharedPreferences) // You can verify specific interactions with the SharedPreferences if needed
        }
    }
}

@RunWith(AndroidJUnit4::class)
class SideMenuNavBarActivityTest {
    private lateinit var activity: SideMenuNavBarActivity

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var preferenceManager: PreferenceManager


    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        activity = SideMenuNavBarActivity()

        // Set up the shared preferences mock
        whenever(PreferenceManager.getDefaultSharedPreferences(activity)).thenReturn(sharedPreferences)
    }

    @Test
    fun testOnCreate() {
        // Arrange
        val savedInstanceState: Bundle = mock()

        // Act
        activity.onCreate(savedInstanceState)

        // Assert
        verify(PreferenceManager.getDefaultSharedPreferences(activity)).also {
            verify(sharedPreferences) // You can verify specific interactions with the SharedPreferences if needed
        }
    }
}

@RunWith(AndroidJUnit4::class)
class ImageViewerActivityTest {
    private lateinit var activity: ImageViewerActivity

    @Before
    fun setup() {
        activity = ImageViewerActivity()
    }

    @Test
    fun testOnCreate() {
        // Arrange
        val savedInstanceState: Bundle = mock()
        val intent = Intent(ApplicationProvider.getApplicationContext<Context>(), ImageViewerActivity::class.java)
        intent.putStringArrayListExtra("imagePaths", ArrayList())

        // Set the intent for the activity
        activity.intent = intent
        // Act
        activity.onCreate(savedInstanceState)

        // Assert
        // Verify that the activity was created successfully (Add meaningful assertions)
    }
}

@RunWith(AndroidJUnit4::class)
class ImageAdapterTest {
    private lateinit var adapter: ImageAdapter

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var parent: ViewGroup

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        adapter = ImageAdapter(context, ArrayList()) { /* Do nothing */ }
    }

    @Test
    fun testOnCreateViewHolder() {
        // Act
        adapter.onCreateViewHolder(parent, 0)

        // Assert
        // Verify that the view holder was created successfully (Add meaningful assertions)
    }
}

@RunWith(AndroidJUnit4::class)
class NotificationsAdapterTest {
    private lateinit var adapter: NotificationsAdapter

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var parent: ViewGroup

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        adapter = NotificationsAdapter(context, ArrayList(), ArrayList()) { /* Do nothing */ }
    }

    @Test
    fun testOnCreateViewHolder() {
        // Act
        adapter.onCreateViewHolder(parent, 0)

        // Assert
        // Verify that the view holder was created successfully (Add meaningful assertions)
    }
}
