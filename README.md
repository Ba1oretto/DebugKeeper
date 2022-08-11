# DebugKeeper

## DebugKeeper ![](https://img.shields.io/tokei/lines/github/Ba1oretto/DebugKeeper?style=plastic) ![](https://img.shields.io/github/license/Ba1oretto/DebugKeeper?style=plastic) ![](https://img.shields.io/github/v/release/Ba1oretto/DebugKeeper?style=plastic) ![](https://img.shields.io/github/last-commit/Ba1oretto/DebugKeeper?style=plastic) ![](https://img.shields.io/github/languages/top/Ba1oretto/DebugKeeper?style=plastic)

Keep the client-server connection state when you debug in IntelliJ

## How To (Plugin Developers)

Download the jar and put it into your {serverdir}/plugins folder.

To enable the plugin, add `-Ddebugkeeper.enable=true` to VM options.

To enable logger, add `-Ddebugkeeper.log=true` to VM options.

![](https://user-images.githubusercontent.com/66552396/184092412-98b23be7-6ccd-4149-ba8a-be4c13a7caf0.png)

{% hint style="danger" %}
If the `debugkeeper.enable` not set, false by default, the plugin will have no effect.
{% endhint %}

{% hint style="warning" %}
In order not to block all threads, go to IntelliJ. Use `shift + left-click` to define or `right-click` edit a breakpoint. Select `Suspend` and check the `Thread` option.
{% endhint %}

![](https://user-images.githubusercontent.com/66552396/184091669-e340e4a3-b464-4f02-87cd-4ed75fc58eba.gif)
