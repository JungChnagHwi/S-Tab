import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ssafy.stab.screens.space.NoteListViewModel
import java.lang.IllegalArgumentException

class NoteListViewModelFactory(private val folderId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteListViewModel(folderId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
