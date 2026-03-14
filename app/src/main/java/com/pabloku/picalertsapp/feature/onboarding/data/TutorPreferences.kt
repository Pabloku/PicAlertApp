package com.pabloku.picalertsapp.feature.onboarding.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Singleton
class TutorPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) : TutorEmailLocalDataSource {

    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile(DATASTORE_NAME) }
    )

    private val tutorEmail: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[TUTOR_EMAIL_KEY]
        }

    override fun getTutorEmail(): Flow<String?> = tutorEmail

    override suspend fun saveTutorEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[TUTOR_EMAIL_KEY] = email
        }
    }

    private companion object {
        const val DATASTORE_NAME = "tutor_preferences"
        val TUTOR_EMAIL_KEY = stringPreferencesKey("tutor_email")
    }
}
