package org.odk.collect.audiorecorder

import android.app.Application
import android.content.Context
import android.media.MediaRecorder
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import org.odk.collect.async.CoroutineScheduler
import org.odk.collect.async.Scheduler
import org.odk.collect.audiorecorder.mediarecorder.MediaRecorderRecordingResource
import org.odk.collect.audiorecorder.recorder.Recorder
import org.odk.collect.audiorecorder.recorder.RecordingResourceRecorder
import org.odk.collect.audiorecorder.recording.AudioRecorderViewModelFactory
import org.odk.collect.audiorecorder.recording.internal.AudioRecorderService
import org.odk.collect.audiorecorder.recording.internal.RecordingRepository
import javax.inject.Singleton

private var _component: AudioRecorderDependencyComponent? = null

internal fun Context.getComponent(): AudioRecorderDependencyComponent {
    return _component.let {
        if (it == null && applicationContext is RobolectricApplication) {
            throw IllegalStateException("Dependencies not specified!")
        }

        if (it == null) {
            val newComponent = DaggerAudioRecorderDependencyComponent.builder()
                .application(applicationContext as Application)
                .build()

            _component = newComponent
            newComponent
        } else {
            it
        }
    }
}

@Component(modules = [AudioRecorderDependencyModule::class])
@Singleton
internal interface AudioRecorderDependencyComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun dependencyModule(audioRecorderDependencyModule: AudioRecorderDependencyModule): Builder

        fun build(): AudioRecorderDependencyComponent
    }

    fun inject(activity: AudioRecorderService)
    fun inject(activity: AudioRecorderViewModelFactory)

    fun recordingRepository(): RecordingRepository
}

@Module
internal open class AudioRecorderDependencyModule {

    @Provides
    open fun providesRecorder(application: Application): Recorder {
        return RecordingResourceRecorder(application.cacheDir) { MediaRecorderRecordingResource(MediaRecorder()) }
    }

    @Provides
    @Singleton
    open fun providesRecordingRepository(): RecordingRepository {
        return RecordingRepository()
    }

    @Provides
    open fun providesScheduler(application: Application): Scheduler {
        return CoroutineScheduler(Dispatchers.Main, Dispatchers.IO)
    }
}

internal fun RobolectricApplication.clearDependencies() {
    _component = null
}

internal fun RobolectricApplication.setupDependencies(module: AudioRecorderDependencyModule) {
    _component = DaggerAudioRecorderDependencyComponent.builder()
        .application(this)
        .dependencyModule(module)
        .build()
}
