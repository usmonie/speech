package wtf.speech.feature.passcode.ui.confirm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.delay
import wtf.speech.compass.core.Extra
import wtf.speech.compass.core.LocalRouteManager
import wtf.speech.compass.core.Screen
import wtf.speech.compass.core.ScreenBuilder
import wtf.speech.core.ui.ContentState
import wtf.speech.feature.passcode.ui.PasscodeContent
import wtf.speech.features.passcode.domain.usecase.CheckPasscodesEqualsUseCase

class ConfirmPasscodeScreen private constructor(private val viewModel: ConfirmPasscodeViewModel) : Screen() {
    override val id: String
        get() = ID

    @Composable
    override fun Content() {
        val state by viewModel.state.collectAsState()
        val passcode = state.enteredPasscode

        LaunchedEffect(state) {
            if (state.contentState is ContentState.Error<*, *>) {
                delay(600L)
                passcode.clear()
            }
        }

        PasscodeContent(
            onPasscodeEntered = passcode::add,
            title = "Create Passcode",
            onDeletePressed = passcode::removeLastOrNull,
            passcodeScreenState = state
        )
    }

    public data class ConfirmPasscodeExtra(val passcode: List<Int>) : Extra {
        override val key: String = KEY
        override val data: Any = passcode

        companion object {
            const val KEY = "passcode"
        }
    }

    public companion object Builder : ScreenBuilder {
        const val ID = "ConfirmPasscode"
        override val id: String
            get() = ID

        override fun build(params: Map<String, String>?, extra: Extra?): Screen {
            val passcode = extra?.data as? ConfirmPasscodeExtra ?: throw IllegalArgumentException("Illegal arguments passed: $extra")
            val checkPasscodeUseCase = CheckPasscodesEqualsUseCase()
            return ConfirmPasscodeScreen(ConfirmPasscodeViewModel(passcode, checkPasscodeUseCase))
        }
    }
}
