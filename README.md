# Quest Speedrunning Auto Splitter

Using this plugin requires the LiveSplit program with the LiveSplit server component.

Installation and setup guide can be found here:

[LiveSplit](https://livesplit.org/downloads/)

[LiveSplit Server](https://github.com/LiveSplit/LiveSplit.Server)

## How to use
Download LiveSplit and the LiveSplit server component.

Turn the plugin on and make sure the port in the plugin settings match your LiveSplit server port.

![config](readme_images/config.png)

Start LiveSplit and start the LiveSplit server (right click LS -> control -> start server).
Make sure to add the LS server to your layout, otherwise you won't see "start server" under control.

![lsserver](readme_images/livesplit.png)

Open the sidebar and click "Connect".
If the status turns green it means you have a connection to your LiveSplit server.
If it stays red something went wrong, most likely you did not start the LiveSplit server
or you have mismatching ports in the plugin settings and the LiveSplit server settings.

![sidebar](readme_images/panel.png)

If your status is green you are good to go.

## Splits
Make sure you have `Compare Against > Game Time` selected on LiveSplit, or the timer will be slightly out of sync.