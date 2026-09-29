# Sky Islands（空岛世界）

在 Minecraft **创建新世界**界面新增可选择的空岛世界类型：主世界和下界都是无限虚空，**末地保持原版**，**生物群系保持原版正常分布**，**种子照常随机或输入**。支持 1.21.1 / 1.21.11 × Fabric / NeoForge 四个构建。

## 内置世界类型

| 类型 | 起始内容 |
|---|---|
| **经典空岛 Classic** | 5×5 双层草方块岛 + 中心基岩锚 + 橡树 + 箱子（熔岩桶、冰）——经典圆石机开局 |
| **小型空岛 Small** | 3×3 小岛 + 橡树 + 箱子（熔岩桶、冰×2、甘蔗、南瓜/西瓜种子、面包、仙人掌）——紧凑开局 |
| **单块空岛 Single Block** | 一块草方块 + 一株树苗——硬核挑战 |

## 维度

- **主世界**：无限虚空 + 出生点起始岛；
- **下界**：无限虚空。**玩家每次抵达下界时，脚下自动确保一块 5×4×3 萤石平台**（只填充空气/可替换方块，绝不破坏地狱门门框和玩家建筑；已站在萤石上则跳过）。生物群系保持原版下界分布；
- **末地**：完全原版（主岛、黑曜石柱、末影龙、外岛照常）。

其他共同点：

- 起始岛在世界首次启动时生成一次（saved-data 标记保证**永不重建**，箱子被搬空也不会在重启后复活）；
- 种子与普通世界完全同管线：创建界面随机/输入均可。

## 使用

**单人**：把对应 jar 放进 `mods/`，创建新世界时点"世界类型"按钮轮换到 经典空岛 / 小型空岛 / 单块空岛 即可。

**专用服务器**：`server.properties` 里写：

```properties
level-type=skyislands\:classic
```

可选值：`skyislands:classic` / `skyislands:oldschool`（小型空岛） / `skyislands:single`。世界首次启动时自动建岛并把世界出生点设到岛上（8, 64, 8）。

## 构建

每个子项目独立构建（与组织内其他 mod 相同的约定）：

```bash
cd 1.21.1/fabric   # 或 1.21.1/neoforge、1.21.11/fabric、1.21.11/neoforge
GRADLE_USER_HOME=~/.gradle-skyislands ./gradlew build
# 产物：build/libs/skyislands-<loader>-<mc>-<version>.jar
```

> 若机器配置了全局 Gradle 镜像 init 脚本（如阿里云），NeoForge 依赖会解析失败，务必用隔离的 `GRADLE_USER_HOME`。

## 技术实现（简要）

- 世界类型 = 数据包 world preset（`data/skyislands/worldgen/world_preset/*.json`）+ 追加进 `#minecraft:normal` 标签，出现在原版世界类型轮换器；
- 虚空地形 = 自定义 `noise_settings`（`final_density` 恒 0、`default_fluid`=air、`spawn_target` 空）；1.21.11 的 noise_router 键 `preliminary_surface_level` 与 1.21.1 的 `initial_density_without_jaggedness` 差异按版本各维护一份；
- 下界 = 世界预设里的 `minecraft:the_nether` 维度 + 虚空 `noise_settings`（生物群系引用原版 `minecraft:nether` 多噪声预设）；萤石平台 = 玩家切换维度进下界的事件里按落点确保（原版 `PortalForcer` 建门只放门框不垫地）；
- 末地 = 世界预设直接引用原版 `minecraft:end` 生成器；
- 起始岛 = 服务端 `ServerStarted` 时由 mod 代码放置（固定坐标、确定性布局），随后把世界出生点设到岛上；`SavedData` 一次性标记防重建。

## 验证记录

- 1.21.1 Fabric 专用服务器 E2E：岛屿方块/基岩锚/树/箱子、箱内物资、远处虚空无地形、出生群系非 void、输入种子生效（10/10）；机器人加入直接出生在岛上（2/2）；重启后不重建。
- 下界：维度存在且群系为真实下界群系、远处无地形；机器人跨维度抵达后 5×4×3 萤石平台精确出现在落点下方（顶层 y-1，边界与底层方块逐一核对），机器人站在平台上；换地点再次进入会生成第二块平台；同维度内传送不触发。
- 末地：维度存在且为主岛群系（原版生成器）。
- 1.21.11 Fabric 专用服务器：`level-type` 建岛、种子生效、下界/末地维度与群系核对通过。
- NeoForge 两侧编译通过（未做启动冒烟）。

## License

MIT
