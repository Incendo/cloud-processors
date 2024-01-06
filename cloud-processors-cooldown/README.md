# cloud-processors-cooldown

Postprocessor that adds command cooldowns.

## Installation

cloud-processors-cooldown is not yet available on Maven Central.

## Usage

The cooldown system is managed by a `CooldownManager`, so the first step is to create an instance of that:
```java
CooldownManager<YourSenderType> cooldownManager = CooldownManager.of(configuration);
```
The configuration is an instance of `CooldownConfiguration`. Refer to the JavaDocs for information about specific options,
but an example would be:
```java
CooldownConfiguration configuration = CooldownConfiguration.<YourSenderType>builder()
        .repository(CooldownRepository.forMap(new HashMap<>()))
        .cooldownNotifier(notifier)
        .build();
```
The notifier is an interface that is invoked when a sender is prevented from using a command due to an
active cooldown.

The repository stores active cooldowns for a command sender in the form of cooldown profiles.
The cooldowns are grouped by their `CooldownGroup`, by default a unique group will be created per command.
You may create a named group by using `CooldownGroup.named(name)`. Commands that use the same cooldown group
will have their cooldowns shared by the command sender.

You may create a repository from a map, `CloudCache` or even implement your own. If you want to persist the cooldowns
across multiple temporary sessions then you may use a mapping repository to store the cooldown profiles for a persistent key,
rather than the potentially temporary command sender objects:
```java
CooldownRepository repository = CooldownRepository.mapping(
        sender -> sender.uuid(),
        CooldownRepository.forMap(new HashMap<UUID, CooldownProfile>())
);
```

You may also customize how the cooldown profiles are created by passing a `CooldownProfileFactory` to the `CooldownConfiguration`.

If you want to have the cooldowns automatically removed from the repository to prevent unused profiles from taking up memory you
may register a `ScheduledCleanupCreationListener`:
```java
CooldownRepository repository = CooldownRepository.forMap(new HashMap<>());
ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
CooldownConfiguration configuration = CooldownConfiguration.<YourSenderType>builder()
        // ...
        .repository(repository)
        .addCreationListeners(new ScheduledCleanupCreationListener(executorService, repository))
        .build();
```

You then need to register the postprocessor:
```java
commandManager.registerCommandPostProcessor(cooldownManager.createPostprocessor());
```

### Builders

The cooldowns are configured using a `Cooldown` instance:
```java
Cooldown cooldown = Cooldown.of(DurationFunction.constant(Duration.ofMinutes(5L)));
Cooldown cooldown = Cooldown.of(DurationFunction.constant(Duration.ofMinutes(5L)), CooldownGroup.named("group-name"));
```
which can then be applied to the command by either manually setting the meta value:
```java
commandBuilder.meta(CooldownManager.META_COOLDOWN_DURATION, cooldown);
```
or by applying the cooldown to the builder:
```java
commandBuilder.apply(cooldown);
```

### Annotations

Annotated commands may use the `@Cooldown` annotation:
```java
@Cooldown(duration = 5, timeUnit = ChronoUnit.MINUTES, group = "some-group")
public void yourCommand() {
    // ...
}
```
You need to install the builder modifier for this to work:
```java
CooldownBuilderModifier.install(annotationParser);
```
