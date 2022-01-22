![EasierBackup Banner](https://melluh.com/img/easierbackup/banner2.png)

### Why?

Every server owner should be backing up their server files, no matter the size of their server. Backups are critical in protecting your server, because you never know when disaster will strike. EasierBackup makes this easy and available to everyone.

### Features

* Compresses your files, saving a lot of space
* Does not store unnecessary files (allows you to exempt files and folders from the backup)
* Automatically removes old backups
* Ability to execute terminal commands (or in-game commands) when backup is finished
  * Can be combined with ``rclone`` to store your files on *over 40 cloud storage products*
* Powerful schedule system - automatically runs your backup whenever you want
* In-game loading bar to show backup progress
* Pauses autosave during backup to prevent backup corruption
* Compatible with Windows and Linux

### Getting Started
1. Simply download & install the latest release (available on [SpigotMC](https://www.spigotmc.org/resources/easierbackup.82921/)) as you would any other plugin.
2. Run ``/backup start`` to start creating your first backup. Players with the appropiate permission will see a progress bar in-game, and you will also see periodic status updates in the console log.
3. Finished? You can see your new backup with ``/backup list``. Congrats, you're now one step closer to being protected from data loss! Check out the ``config.yml`` to customize your backups, like automatically running them at a specified time or excluding files you don't need.
