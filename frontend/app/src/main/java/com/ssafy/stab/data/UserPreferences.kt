import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class UserPreferences(private val context: Context) {  // Context를 생성자를 통해 받음
    private val Context.dataStore by preferencesDataStore(name = "user_prefs")  // Context 확장으로 DataStore 생성

    companion object {
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val NICKNAME = stringPreferencesKey("nickname")
        val PROFILE_IMG = stringPreferencesKey("profile_img")
        val ROOT_FOLDER_ID = stringPreferencesKey("root_folder_id")
    }

    // 로그인 상태 저장
    suspend fun saveLoginDetails(
        loggedIn: Boolean,
        accessToken: String,
        nickname: String,
        profileImg: String,
        rootFolderId: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[LOGGED_IN] = loggedIn
            preferences[ACCESS_TOKEN] = accessToken
            preferences[NICKNAME] = nickname
            preferences[PROFILE_IMG] = profileImg
            preferences[ROOT_FOLDER_ID] = rootFolderId
        }
    }

    // 로그인 상태 불러오기
    val loginDetails: Flow<LoginState> = context.dataStore.data.map { preferences ->
        val isLoggedIn = preferences[LOGGED_IN] ?: false
        val token = preferences[ACCESS_TOKEN] ?: ""
        val nickname = preferences[NICKNAME] ?: ""
        val profileImg = preferences[PROFILE_IMG] ?: ""
        val rootFolderId = preferences[ROOT_FOLDER_ID] ?: ""
        LoginState(isLoggedIn, token, nickname, profileImg, rootFolderId)
    }
}
data class LoginState(
    val isLoggedIn: Boolean,
    val accessToken: String,
    val nickname: String,
    val profileImg: String,
    val rootFolderId: String
)