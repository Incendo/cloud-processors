# cloud-processor-requirements

Command requirement system for [Cloud v2](https://github.com/incendo/cloud).

The requirements are evaluated before
the command is executed to determine whether the command sender should be able to execute the command. The requirements
are defined on a per-command basis.

## Installation

Snapshots are available on the Sonatype Snapshots Repository:

```xml
<repository>
  <id>sonatype-snapshots</id>
  <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
</repository>

<dependency>
    <groupId>org.incendo</groupId>
    <artifactId>cloud-processors-requirements</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Usage

You create requirements by implementing the `Requirement` interface. It is recommended to create an intermediary
requirement interface that extends `Requirement` that can contain shared logic. This also reduces verbosity introduced
by the generic types. Example:
```java
public interface YourRequirementInterface implements Requirement<YourSenderType, YourRequirementInterface> {
    
    // Example method
    @NonNull String errorMessage();
}
```
you can then create a requirement:
```java
public final class YourRequirement implements YourRequirementInterface {
    
    @Override
    public @NonNull String errorMessage() {
        return "not cool enough";
    }
    
    @Override
    public boolean evaluateRequirement(final @NonNull CommandContext<YourSenderType> context) {
        return false; // You should probably put some logic here :)
    }
}
```

You then need to create a `CloudKey<Requirements<YourSenderType, YourRequirementInterface>` which is used to store
the requirements in the command meta and for the processor to access the stored requirements:
```java
public static final CloudKey<Requirements<YourSenderType, YourRequirementInterface>> REQUIREMENT_KEY = CloudKey.of(
        "requirements",
        new TypeToken<CloudKey<Requirements<YourSenderType, YourRequirementInterface>>>() {}
);
```

You then need to create an instance of the postprocessor and register it to your command manager:
```java
final RequirementPostprocessor<YourSenderType, YourRequirementInterface> postprocessor = RequirementPostprocessor.of(
        REQUIREMENTS_KEY,
        new YourFailureHandler()
);
commandManager.registerPostprocessor(postprocessor);
```
the failure handler gets invoked when the command sender fails to meet a requirement:
```java
public final class YourFailureHandler implements RequirementFailureHandler<YourSenderType, YourRequirementInterface> {

    @Override
    public void handleFailure(
            final @NonNull CommandContext<YourSenderType> context,
            final YourRequirementInterface requirement
    ) {
        context.sender().sendMessage("Requirement failed: " + requirement.errorMessage());
    }
}
```

You then need to register the requirements to your command. This step depends on whether you use
[builders](#builders) or [annotations](#annotations).

### Builders

You have two different options when it comes to registering requirements to commands using the command builders.
You may store the requirements directly:
```java
commandBuilder.meta(REQUIREMENT_KEY, Requirements.of(requirement, requirement1, ...));
```

or by using the `RequirementApplicable` system:
```java
// Store this somewhere:
RequirementApplicable.RequirementApplicableFactory<YourSenderType, 
        YourRequirementInterface> factory = RequirementApplicable.factory(REQUIREMENT_KEY);

// Then register the requirements:
commandBuilder.apply(factory.create(requirement, requirement1, ...));
```

### Annotations

When using `cloud-annotations` you may use the `RequirementBindings` system to register bindings between
annotations and requirements:
```java
// Create some annotation:
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface YourAnnotation {
    // ...
}
    
// Then register a binding for it:
RequirementBindings.create(this.annotationParser, REQUIREMENT_KEY).register(
        YourAnnotation.class,
        annotation -> new YourRequirement()
);

// Then annotate a method with it:
@YourAnnotation
@Command("command")
public void commandMethod() {
    // ...
}
```
