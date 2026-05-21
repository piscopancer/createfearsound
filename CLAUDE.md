# Create: Fear, Sound — разбор проекта

Аддон для мода **Create** (Minecraft 1.21.1, NeoForge 21.1.217), цель — проигрывать пользовательскую музыку через механизмы Create. Идея: записать аудио на «кусочек плёнки» (`TapePiece`), скомпоновать треки в «кассету» (`Cassette`), а блок-проигрыватель (`TapePlayer`) воспроизводит её.

## 0. Правила разработки (важно)

Мод должен **выглядеть и ощущаться как Create**. Это не косметика — это базовое требование к любому новому контенту.

**UX-каноны Create, которые надо повторять:**

- **Цветные обводки на блоках при наведении/выделении** — рендерятся линиями. Не белая ванильная рамка, а свой outline-рендер с цветом, как у Schematicannon, Mechanical Arm, Smart Pipes и пр.
- **Очки инженера (Engineer's Goggles)** показывают доп. инфо о блоках аддона прямо в HUD — статус, скорость, привязки, текущий трек. Любой блок с состоянием обязан иметь goggle-оверлей.
- **Минимум интерфейсов.** GUI открывается ТОЛЬКО для чего-то маленького и точечного (одно поле, пара кнопок). Списки, статусы, диагностика — через goggles-overlay, tooltip, ponder-сцену, не через AbstractContainerScreen.
- **Цельный визуальный язык:** металлические текстуры, латунь/андезит, шрифт и иконки Create.

**Как создавать контент:**

1. **Сначала ищи готовое в Create**, а не пиши своё. Перед написанием класса обязательно проверить:
   - Распакованный jar Create в gradle-кэше (`~/.gradle/caches/modules-2/files-2.1/com.simibubi.create/`)
   - Подключённые исходники Create / Ponder / Flywheel / Registrate (IDE: Go to → Class, ищи аналог)
   - Базовые классы Create: `KineticBlock`, `KineticBlockEntity`, `SmartBlockEntity`, `BlockEntityBehaviour`, `IInteractionChecker`, `AbstractContraptionEntity`, `Outliner`, `ValueBoxRenderer`, `GoggleInformationProvider`, `IHaveGoggleInformation`, `BlockStressValues`, `AllShapes`, `AllInteractionBehaviours`, ProcessingRecipe*, AllPartialModels и т.д.
   - Веб-поиск: «Create addon X», «IHaveGoggleInformation example», «Create outline rendering custom block» — смотри как делают живые аддоны на GitHub и форумах.
2. **Наследуй классы Create**, не дублируй их. Кастомный блок с движением — `KineticBlock`/`HorizontalKineticBlock`. Кастомный BE — `KineticBlockEntity`/`SmartBlockEntity`. Доп. поведение — `BlockEntityBehaviour`, а не свой костыль. Outline — через Create `Outliner`. Goggle-инфо — реализуй `IHaveGoggleInformation`/`IHaveHoveringInformation` на BE.
3. **Своих абстракций не плодить.** Если в Create есть схожий механизм — переиспользуй его API. Свой код только когда в Create реально такого нет, и даже тогда — по образцу Create-классов.
4. **Перед тем как закоммитить новую механику**: показать ссылку/путь к Create-классу или аддону-референсу, на котором она основана.

Если рискуешь нарушить эти правила — спрашивай у пользователя ДО реализации.

## 1. Стек и сборка

- **build.gradle**: NeoForge ModDev 2.0.137, Java 21, Parchment-маппинги.
- **gradle.properties**: `minecraft_version=1.21.1`, `neo_version=21.1.217`, `create_version=6.0.9-215`, `ponder=1.0.81`, `flywheel=1.0.6`, `jei=19.27.0.339`, `registrate=MC1.21-1.3.0+67`.
- **Зависимости компиляции**: Create (slim), Ponder, Flywheel API, Registrate, JEI API, **Lombok** (annotation processor, используется `@Builder` в записях/payload'ах).
- **Шаблон мета**: [src/main/templates/META-INF/neoforge.mods.toml](src/main/templates/META-INF/neoforge.mods.toml) — модид `createfearsound`, обязательная зависимость от `create [6.0.9,6.1.0)`, `minecraft 1.21.1`, `neoforge`.
- **Datagen**: выводит в `src/generated/resources`, прицеплено к `sourceSets.main.resources`.

## 2. Структура пакетов

```
dev.piscopancer.createfearsound
├── CFS.java                       — точка входа @Mod, modid="createfearsound"
├── Config.java                    — пример NeoForge ModConfigSpec (не используется по делу)
├── Util.java                      — modResLoc / texturePath хелперы
├── client/
│   ├── CFSClient.java             — @Mod(dist=CLIENT): ItemProperties + регистрация Screen
│   └── gui/
│       ├── CassetteMenu.java      — AbstractContainerMenu (20 слотов)
│       ├── CassetteScreen.java    — AbstractContainerScreen для кассеты
│       └── TapePieceScreen.java   — Screen для ввода title/author/url
├── common/
│   ├── blocks/
│   │   └── TapePlayerBlockEntity.java   — пустой стаб BlockEntity
│   ├── data/
│   │   ├── TrackData.java         — record {title, author, url, duration} + Codec/StreamCodec
│   │   └── CassetteData.java      — record {label, note, List<TrackData>} + Codec/StreamCodec
│   ├── items/
│   │   ├── Cassette.java          — Item + enum Color {None, Red, Green}
│   │   └── TapePiece.java         — Item с tooltip + открытием экрана
│   ├── recipes/
│   │   ├── CassetteMixingRecipe.java     — наследник Create MixingRecipe
│   │   └── CassetteMixingSerializer.java — Codec/StreamCodec для рецепта
│   └── registries/
│       ├── ModRegistries.java     — агрегатор register(modEventBus)
│       ├── ItemsRegistry.java     — CASSETTE, TAPE_PIECE
│       ├── BlocksRegistry.java    — пустой регистр (заглушка)
│       ├── CreativeModTabsRegistry.java — вкладка креатива
│       ├── MenuTypesRegistry.java — CASSETTE_MENU
│       ├── DataComponentsRegistry.java — TAPE_PIECE, CASSETE, COLOR_DATA_COMPONENT
│       └── CreateSerializersRegistry.java — CASSETE_MIXING
├── server/
│   ├── Payloads.java              — регистрация хендлеров пакетов
│   └── payloads/
│       ├── TrackPayload.java      — клиент→сервер для TapePiece
│       └── CassettePayload.java   — объявлен, но НЕ регистрируется
└── datagen/
    ├── Datagen.java               — точка входа GatherDataEvent
    ├── CFSItemModelProvider.java  — модели + override по compoment color
    ├── CFSLangProvider.java       — en_us и ru_ru
    └── CFSRecipeProvider.java     — содержит 3 рецепт-провайдера
```

## 3. Поток данных: как файлы связаны

### Регистрация (cold start)

[CFS.java:17](src/main/java/dev/piscopancer/createfearsound/CFS.java#L17) → [ModRegistries.register](src/main/java/dev/piscopancer/createfearsound/common/registries/ModRegistries.java#L6) последовательно подключает все `DeferredRegister`:

- `ItemsRegistry` → `Cassette`, `TapePiece`
- `BlocksRegistry` → пусто
- `CreativeModTabsRegistry` → вкладка `createfearsound_tab` с обоими предметами
- `MenuTypesRegistry` → `cassette_menu`
- `DataComponentsRegistry` → три компонента данных
- `CreateSerializersRegistry` → сериализатор `cassette_mixing`

`CFSClient` через `@EventBusSubscriber(Dist.CLIENT)` отдельно:

- Регистрирует `ItemProperties` `createfearsound:color` для модели Cassette ([CFSClient.java:42-52](src/main/java/dev/piscopancer/createfearsound/client/CFSClient.java#L42-L52)).
- Регистрирует `CassetteScreen` для `CASSETTE_MENU` ([CFSClient.java:57-59](src/main/java/dev/piscopancer/createfearsound/client/CFSClient.java#L57-L59)).

### Запись трека (Tape Piece)

1. Игрок right-click'ает `TapePiece` → [TapePiece.use](src/main/java/dev/piscopancer/createfearsound/common/items/TapePiece.java#L24-L30) на клиенте открывает `new TapePieceScreen(stack)`.
2. В [TapePieceScreen.init](src/main/java/dev/piscopancer/createfearsound/client/gui/TapePieceScreen.java#L28) — три `EditBox` (title/author/url) и кнопка «Сохранить». Кнопка активна только при валидном http(s) URL ([TapePieceScreen.java:82-91](src/main/java/dev/piscopancer/createfearsound/client/gui/TapePieceScreen.java#L82-L91)).
3. По клику шлётся `TrackPayload` через `PacketDistributor.sendToServer` ([TapePieceScreen.java:46-54](src/main/java/dev/piscopancer/createfearsound/client/gui/TapePieceScreen.java#L46-L54)).
4. Хендлер в [Payloads.register](src/main/java/dev/piscopancer/createfearsound/server/Payloads.java#L17-L37): берёт предмет в руке, проверяет что это `TAPE_PIECE`, кладёт `TrackData` в дата-компонент `createfearsound:tape_piece`.
5. Дальше `TapePiece.appendHoverText` ([TapePiece.java:33-48](src/main/java/dev/piscopancer/createfearsound/common/items/TapePiece.java#L33-L48)) читает компонент и рисует tooltip с названием/автором/длительностью.

### Кассета и меню (sneak + right-click)

1. `Cassette.use` ([Cassette.java:25-36](src/main/java/dev/piscopancer/createfearsound/common/items/Cassette.java#L25-L36)) при `player.isCrouching()` на сервере открывает `SimpleMenuProvider(CassetteMenu::new)`.
2. `CassetteMenu` создаёт 20 слотов (2×10) в локальном `SimpleContainer` ([CassetteMenu.java:20-27](src/main/java/dev/piscopancer/createfearsound/client/gui/CassetteMenu.java#L20-L27)). Инвентарь игрока закомментирован.
3. `CassetteScreen` рисует фон из `textures/gui/cassette.png` (текстуры в репо НЕТ).

### Цвет кассеты

- Дата-компонент `color` (`Cassette.Color` enum) сериализуется как строка через `Codec.STRING.xmap` ([DataComponentsRegistry.java:28-50](src/main/java/dev/piscopancer/createfearsound/common/registries/DataComponentsRegistry.java#L28-L50)).
- В `CFSClient` `ItemProperties` мапит `None→0`, `Red→1`, `Green→2`.
- В `CFSItemModelProvider` под эти значения вешаются override-модели `cassette_red` / `cassette_green` (есть и текстуры, и сгенерированные json-модели).
- Но **никто этот компонент не пишет** — нигде в коде нет `stack.set(COLOR_DATA_COMPONENT...)`. Цвет всегда `None → 0`.

### Рецепты (Create)

- Datagen генерирует три рецепта: ванильный shaped «кассета» ([CFSRecipeProvider.java:27-34](src/main/java/dev/piscopancer/createfearsound/datagen/CFSRecipeProvider.java#L27-L34)), pressing Kelp → TapePiece ([CFSRecipeProvider.java:45-47](src/main/java/dev/piscopancer/createfearsound/datagen/CFSRecipeProvider.java#L45-L47)), mixing Paper+Cassette ([CFSRecipeProvider.java:55-62](src/main/java/dev/piscopancer/createfearsound/datagen/CFSRecipeProvider.java#L55-L62)). Сгенерированы JSON в `src/generated/resources/data/create/recipe/...`.
- Кастомный сериализатор `CassetteMixingSerializer` зарегистрирован в `Registries.RECIPE_SERIALIZER`, но **JSON-рецепты, использующие его, не создаются**. То есть `CassetteMixingRecipe` существует в классах, но в игре его никто не инстанцирует.
- Идея класса: при mixing'е paper-с-именем + кассета записывает `note = customName` в `CassetteData` результата ([CassetteMixingRecipe.java:35-58](src/main/java/dev/piscopancer/createfearsound/common/recipes/CassetteMixingRecipe.java#L35-L58)).

### Конфиг

[Config.java](src/main/java/dev/piscopancer/createfearsound/Config.java) — нерабочий шаблон из примера (logDirtBlock, magicNumber). Регистрируется в `CFS` как `ModConfig.Type.COMMON`, но нигде не читается. Файл `run/config/createfearsound-common.toml` создаётся при первом запуске.

## 4. Что реализовано (готово)

- Инициализация мода и реестры (`CFS`, `ModRegistries`).
- Предметы `Cassette` и `TapePiece`, креативная вкладка.
- Дата-компоненты: `tape_piece` (TrackData), `cassette` (CassetteData), `color` (enum).
- Network payload: клиент→сервер `TrackPayload` + хендлер записи в дата-компонент TapePiece.
- `TapePieceScreen` с валидацией http(s) URL и UI на трёх полях.
- Tooltip на `TapePiece` со считыванием компонента.
- `CassetteMenu` (20 пустых слотов) + `CassetteScreen` (с привязкой к фоновой текстуре, которой нет).
- Datagen: модели предметов с override по `color` (включая текстуры `cassette_red.png`, `cassette_green.png`), переводы en_us/ru_ru, JSON-рецепты Create pressing/mixing + ванильный shaped.

## 5. Что НЕ реализовано / сломано / черновое

| Область                                    | Состояние                                                                                                                                                                                                                                                        |
| ------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Воспроизведение звука**                  | Полностью отсутствует. Поле `url` пишется, но никто его не открывает/декодирует. Нет ни OpenAL, ни HTTP-стрима, ни системы звуков. Это ядро функциональности мода и его нет.                                                                                     |
| **Длительность трека**                     | Клиент всегда шлёт `duration: 0` ([TapePieceScreen.java:51](src/main/java/dev/piscopancer/createfearsound/client/gui/TapePieceScreen.java#L51)). На сервере не вычисляется.                                                                                      |
| **`TapePlayer` блок**                      | Класс `TapePlayerBlockEntity` пуст, `BlocksRegistry` ничего не регистрирует, `BlockEntityType` не зарегистрирован. Блок-проигрыватель отсутствует.                                                                                                               |
| **`CassetteMenu`**                         | Слоты прикреплены к локальному `SimpleContainer`, который существует только во время открытия меню (не сохраняется в ItemStack). Инвентарь игрока закомментирован. `quickMoveStack` возвращает `EMPTY`. `stillValid` всегда `true`. Меню фактически бесполезное. |
| **Логика «вставить TapePiece в Cassette»** | Не реализована. `CassetteData.tracks` нигде не модифицируется, кроме как в `CassetteMixingRecipe.assemble`.                                                                                                                                                      |
| **Цвет кассеты**                           | Компонент `color` зарегистрирован, ItemProperties и модели подключены, но **никто не выставляет цвет**. Всегда `None`. Нет крафта/команды/UI для смены цвета.                                                                                                    |
| **`CassettePayload`**                      | Класс есть, но в `Payloads.register` не регистрируется и нигде не отправляется. Мёртвый код.                                                                                                                                                                     |
| **Регистрация `CassetteMixingRecipe`**     | Сериализатор зарегистрирован, но JSON-рецепт, который бы его использовал, в datagen не создаётся. Класс компилируется, но в игру не попадает.                                                                                                                    |
| **`CassetteScreen` фон**                   | Ссылается на `textures/gui/cassette.png`, файла нет в `src/main/resources/assets/createfearsound/textures/`. Будет фиолетово-чёрная клетка.                                                                                                                      |
| **`Config`**                               | Пример из шаблона NeoForge; значения не читаются нигде.                                                                                                                                                                                                          |
| **`Util.java`**                            | Метод `texturePath(Path)` некорректно строит путь (использует `Path.of("textures", subpath.toString())`, что нормально, но `texturePath(String...)` собирает `Path.of("", subpath)` — лишний пустой сегмент). Работает за счёт `toString()`, но хрупко.          |
| **JEI**                                    | Зависимость подключена в `build.gradle`, плагина (`@JeiPlugin`) в коде нет. Категорий/превью рецептов нет.                                                                                                                                                       |
| **Ponder / Flywheel**                      | Зависимости есть, использования нет.                                                                                                                                                                                                                             |
| **GameTest'ы**                             | `runs.gameTestServer` настроен, но тестов нет.                                                                                                                                                                                                                   |
| **Resource Pack `Whimscape`**              | Лежит в `run/resourcepacks/`, к моду не относится — это локальный artwork-пак, добавленный для разработки.                                                                                                                                                       |

## 6. Замеченные мелкие проблемы

- **Опечатка** в имени поля: `CASSETE` (одно «t») в `DataComponentsRegistry` и `CreateSerializersRegistry` — лучше переименовать в `CASSETTE`, иначе компонент сериализуется как `createfearsound:cassette` (правильно), но идентификатор в коде путает.
- В `TapePiece.use` экран открывается на клиенте без `Minecraft.getInstance().execute(...)` — для `use` это ок, но если будет вызываться из другого треда, лопнет.
- `TapePieceScreen.isWebUrl` создаёт `URI`, но результат не использует — проверка фактически идёт по `startsWith` и `contains(".")`. `URI` можно убрать.
- В `CFSRecipeProvider` файл содержит ТРИ package-private класса в одном `.java` (`CFSRecipeProvider`, `CFSPressingRecipeProvider`, `CFSMixingRecipeProvider`) — компилируется, но непривычно; имя файла соответствует только первому.
- `CassetteMixingRecipe.assemble` не учитывает случай, когда нет кассеты во входе — `result.set(...)` запишет пустые tracks. Зависит от `matches`, который проверяет только `paper + custom name`, не требуя кассеты.
- `Cassette.Color` enum не использует ConstantNamingConvention (`None`/`Red`/`Green` вместо `NONE`/`RED`/`GREEN`); работает, но нарушает соглашение Java.

## 7. Структура исходников Create (JAR)

JAR с исходниками лежит в gradle-кеше:
- **Create sources**: `~/.gradle/caches/modules-2/files-2.1/com.simibubi.create/create-1.21.1/6.0.9-215/…/create-1.21.1-6.0.9-215-sources.jar`
- **Ponder/Catnip sources**: `~/.gradle/caches/modules-2/files-2.1/net.createmod.ponder/ponder-neoforge/1.0.81+mc1.21.1/…/ponder-neoforge-1.0.81+mc1.21.1-sources.jar`

Для чтения файла из JAR: `unzip -p <путь>.jar <путь/к/файлу>.java` (через Bash).

### Пакеты Create (`com.simibubi.create`)

| Пакет | Что там |
|-------|---------|
| `com.simibubi.create` | `AllBlocks`, `AllItems`, `AllBlockEntityTypes`, `AllMenuTypes`, `AllShapes`, `AllSpecialTextures`, `AllSoundEvents`, `AllPartialModels`, `AllTags` — все реестры |
| `com.simibubi.create.api.equipment.goggles` | `IHaveGoggleInformation`, `IHaveHoveringInformation`, `IHaveCustomOverlayIcon`, `IProxyHoveringInformation` |
| `com.simibubi.create.api.stress` | API стресса (нагрузка/генерация) |
| `com.simibubi.create.content.kinetics.base` | `KineticBlock`, `KineticBlockEntity`, `HorizontalKineticBlock`, `DirectionalKineticBlock`, `GeneratingKineticBlockEntity` |
| `com.simibubi.create.foundation.blockEntity` | `SmartBlockEntity`, `SyncedBlockEntity`, `IBE` (интерфейс для блоков с BE) |
| `com.simibubi.create.foundation.blockEntity.behaviour` | `BlockEntityBehaviour`, `ValueBox`, `ValueBoxRenderer`, `ValueBoxTransform`, `ScrollValueBehaviour`, `FilteringBehaviour`, `SmartFluidTankBehaviour` |
| `com.simibubi.create.foundation.block` | `IHaveBigOutline`, `BigOutlines`, `IBE`, `WrenchableDirectionalBlock` |
| `com.simibubi.create.foundation.block.render` | Кастомные модели блоков |
| `com.simibubi.create.foundation.gui` | `AllGuiTextures`, `AllIcons`, `AbstractSimiContainerScreen`, `MenuBase` |
| `com.simibubi.create.foundation.gui.widget` | `IconButton`, `ScrollInput`, `Label`, `Indicator` |
| `com.simibubi.create.foundation.utility` | `CreateLang`, `RaycastHelper`, `IInteractionChecker` |
| `com.simibubi.create.foundation.placement` | `PoleHelper` |
| `com.simibubi.create.content.redstone.link` | `LinkBehaviour`, `LinkRenderer`, `IRedstoneLinkable` — паттерн «привязка блоков» |
| `com.simibubi.create.content.processing.recipe` | `ProcessingRecipe`, `ProcessingRecipeBuilder` — базовые рецепты |
| `com.simibubi.create.compat.jei` | Категории JEI, паттерн регистрации |

### Ключевые классы Catnip/Ponder (`net.createmod.catnip`)

| Класс | Что делает |
|-------|-----------|
| `net.createmod.catnip.outliner.Outliner` | Singleton. `getInstance().showAABB(slot, aabb).colored(0xRRGGBB).lineWidth(1/16f).withFaceTexture(AllSpecialTextures.SELECTION)` — цветная обводка блока. TTL по умолчанию = 1 тик, надо вызывать каждый тик. |
| `net.createmod.catnip.outliner.Outline.OutlineParams` | Построитель параметров: `.colored(int)`, `.lineWidth(float)`, `.withFaceTexture(BindableTexture)`, `.lightmap(int)` |
| `net.createmod.catnip.render.BindableTexture` | Интерфейс текстур для outline (реализует `AllSpecialTextures`) |

### Паттерны, которые стоит повторять

- **Highlight при удержании предмета** → `@EventBusSubscriber(bus = Bus.GAME)` + `ClientTickEvent.Pre` + `Outliner.getInstance().showAABB(...)` — см. `LinkRenderer.tick()` и `CFSClientEvents.java`
- **Goggle-оверлей** → `SmartBlockEntity implements IHaveGoggleInformation`, метод `addToGoggleTooltip(List<Component>, boolean)`
- **Поведение блока** → наследовать `BlockEntityBehaviour`, добавить в `SmartBlockEntity.addBehaviours(List)`
- **Подсветка блока по команде** → `HighlightPacket` (Create infra) использует `showAABB` с TTL 200
