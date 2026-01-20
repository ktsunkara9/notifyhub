Building a Native Executable is essentially moving from a "Just-in-Time" world (where Java can find things as it runs) to an "Ahead-of-Time" world (where everything must be proven to exist before the app even starts).

Here is the technical breakdown of the solutions we implemented to fix your build.

1. Dependency Alignment (The "Unresolved Method" Fix)
The most significant build failure was GraalVM encountering "Unresolved Methods" in the AWS SDK. This happened because different parts of your project were pulling in conflicting versions of the AWS SDK.

The Solution: Use the Quarkus Amazon Services BOM. A BOM (Bill of Materials) acts as a single source of truth, forcing all AWS-related libraries to stay in sync so the native linker doesn't see "missing" methods.

2. Correct Extension Discovery
We initially struggled with Could not find artifact errors because the artifact naming for Quarkus AWS extensions changed in version 3.x.

The Solution: We moved to the Quarkiverse group ID and used the specific SQS extension which contains the "Substitutions" (pre-written code patches) GraalVM needs to handle the complex AWS SDK.

3. Solving the HTTP Client Runtime Crash
The AWS SDK's default HTTP client (Apache) is highly reflective and often breaks in Native mode. Even if it builds, it often fails at runtime with Unable to load an HTTP implementation.

The Solution:

Add the lightweight client: We added url-connection-client to the pom.xml.

Explicit Configuration: We told Quarkus to use it in application.properties.

Code Injection: We used @Inject SqsClient instead of new SqsClient() to let the framework apply its native-optimized settings.

4. Forced Class Initialization & Reflection
We faced errors where GraalVM didn't know how to handle certain classes at build time (like Joda-Time or your custom DTOs).

The Solution:

For DTOs: Used @RegisterForReflection on NotificationRequest and HealthResponse. This tells GraalVM: "Don't delete the metadata for these classes; Jackson needs them to map JSON."

For Build-Time Logic: We added --initialize-at-run-time flags for legacy libraries (like Apache NTLM) that weren't compatible with GraalVM's build-time initialization defaults.