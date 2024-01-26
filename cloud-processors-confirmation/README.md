# cloud-processors-confirmation

Postprocessor that adds the ability to require an extra confirmation before executing commands.

## Installation

Snapshots are available on the Sonatype Snapshots Repository:

```xml
<!-- For snapshot releases -->
<repository>
  <id>sonatype-snapshots</id>
  <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
</repository>

<dependency>
    <groupId>org.incendo</groupId>
    <artifactId>cloud-processors-confirmation</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Usage

You have to create an instance of `ConfirmationManager`, which accepts a `ConfirmationConfiguration`:
```java
ConfirmationManager<YourSenderType> confirmationManager = ConfirmationManager.of(configuration);
```
The configuration is created using a builder:
```java
ConfirmationConfiguration<YourSenderType> configuration = ConfirmationManager.<YourSenderType>builder()
        .cache(cache)
        .noPendingCommandNotifier(...)
        .confirmationRequiredNotifier(...)
        .build();
```
The notifiers are consumers that are invoked when different events take place. See the JavaDoc for more information.

The cache is an instance of `CloudCache`, the default implementations are:
- `GuavaCache`: Cache that wraps a Guava cache.
- `CaffeineCache`: Cache that wraps a Caffeine cache.
- `SimpleCache`: Cache that wraps a weak hashmap. This is not recommended to use as it may grow indefinitely and offers very 
  little control.

You then need to register the postprocessor to the command manager:
```java
commandManager.registerCommandPostProcessor(confirmationManager.createPostProcessor());
```

Commands that have the confirmation meta key will now require confirmation before they can be executed.
You may pass a predicate to the configuration that accepts a command context and determines whether the confirmation
should be bypassed. This allows you to use permissions, flags and other methods to conditionally disable the confirmation 
requirement.

You'll likely want to create a confirmation command, which can be done using the command execution handler returned by
the confirmation manager:
```java
commandManager.command(
        commandManager.commandBuilder("confirm")
            .handler(confirmationManager.createExecutionHandler())
);
```

### Builders

To indicate that a command requires confirmation you need to apply the `ConfirmationManager.META_CONFIRMATION_REQUIRED`
meta to the command. You may either to do this by manually applying it:
```java
commandBuilder.meta(ConfirmationManager.META_CONFIRMATION_REQUIRED, true)
```
or by decorating the builder using the confirmation manager:
```java
commandBuilder.apply(confirmationManager)
```

### Annotations

The module ships with a builder modifier that enables the use of the `@Confirmation` annotation. To install this, you
simply do:
```java
ConfirmationBuilderModifier.install(annotationParser);
```
