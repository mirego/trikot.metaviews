@GrabResolver('https://s3.amazonaws.com/mirego-maven/public')
@GrabExclude('org.codehaus.groovy:groovy-all')
@Grab('com.mirego.jenkins:jenkins-jobs:1.2')
import com.mirego.jenkins.jobs.Context

Context context = Context.load(this)
context.standardFolders()

job(context.jobFullName) {
    description("Make a release of trikot.viewmodels")
    logRotator(5)
    scm {
        git {
            branch("${GIT_BRANCH}")
            remote {
                name('origin')
                url("${GIT_URL}")
                credentials('github')
            }
        }
    }
    wrappers {
        credentialsBinding {
            amazonWebServicesCredentialsBinding {
                accessKeyVariable('MAVEN_AWS_KEY')
                secretKeyVariable('MAVEN_AWS_SECRET')
                credentialsId('mirego-maven-aws')
            }
        }
    }
    steps {
        gradle {
            useWrapper()
            makeExecutable()
            tasks(':viewmodels:release')
            switches('-i -s')
        }
    }
    publishers {
        slackNotifier {
            notifyFailure(true)
            notifyBackToNormal(true)
            room(context.slackChannel)
        }
    }
}

job("$context.jobFullName-legacy-1_0_*") {
    description("Make a release of trikot.viewmodels")
    logRotator(5)
    scm {
        git {
            branch("release/1.0.x")
            remote {
                name('origin')
                url("${GIT_URL}")
                credentials('github')
            }
        }
    }
    wrappers {
        credentialsBinding {
            amazonWebServicesCredentialsBinding {
                accessKeyVariable('MAVEN_AWS_KEY')
                secretKeyVariable('MAVEN_AWS_SECRET')
                credentialsId('mirego-maven-aws')
            }
        }
    }
    steps {
        gradle {
            useWrapper()
            makeExecutable()
            tasks(':viewmodels:release')
            switches('-i -s')
        }
    }
    publishers {
        slackNotifier {
            notifyFailure(true)
            notifyBackToNormal(true)
            room(context.slackChannel)
        }
    }
}

job("$context.jobFullName-android-ktx-legacy-1_0_*") {
    description("Make a release of trikot.viewmodels:android-ktx")
    logRotator(5)
    scm {
        git {
            branch("release/1.0.x")
            remote {
                name('origin')
                url("${GIT_URL}")
                credentials('github')
            }
        }
    }
    wrappers {
        credentialsBinding {
            amazonWebServicesCredentialsBinding {
                accessKeyVariable('MAVEN_AWS_KEY')
                secretKeyVariable('MAVEN_AWS_SECRET')
                credentialsId('mirego-maven-aws')
            }
        }
    }
    steps {
        gradle {
            useWrapper()
            makeExecutable()
            tasks(':android-ktx:release')
            switches('-i -s')
        }
    }
    publishers {
        slackNotifier {
            notifyFailure(true)
            notifyBackToNormal(true)
            room(context.slackChannel)
        }
    }
}
