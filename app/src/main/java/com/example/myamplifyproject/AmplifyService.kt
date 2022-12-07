package com.example.myamplifyproject

import android.content.Context
import android.util.Log
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify

interface AmplifyService {
    fun configureAmplify(context: Context)

    fun signUp(state: SignUpState, onComplete: () -> Unit)

    fun verifyCode(state: VerificationCodeState, onComplete: () -> Unit)

    fun login(state: LoginState, onComplete: () -> Unit)

    fun logOut(onComplete: () -> Unit)
}

class AmplifyServiceImpl : AmplifyService {
    override fun configureAmplify(context: Context) {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(context)
            Log.i("SMP", "Configured amplify")
        } catch (e: Exception) {
            Log.e("SMP", "Amplify configuration failed", e)
        }
    }

    override fun signUp(state: SignUpState, onComplete: () -> Unit) {
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), state.email)
            .build()

        Amplify.Auth.signUp(state.username, state.password, options,
            { onComplete() },
            { Log.e("SMP", "Sign Up Error:", it) }
        )
    }

    override fun verifyCode(state: VerificationCodeState, onComplete: () -> Unit) {
        Amplify.Auth.confirmSignUp(
            state.username,
            state.code,
            { onComplete() },
            { Log.e("SMP", "Verification Failed: ", it) }
        )
    }

    override fun login(state: LoginState, onComplete: () -> Unit) {
        Amplify.Auth.signIn(
            state.username,
            state.password,
            { onComplete() },
            { Log.e("SMP", "Login Error:", it) }
        )
    }

    override fun logOut(onComplete: () -> Unit) {
        Amplify.Auth.signOut { onComplete() }
    }
}