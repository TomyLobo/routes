Plugins
-------

#### MetaPlugin
The framework-independent part, like WE's WorldEdit class

#### FrameworkPlugin
The minimal, framework-dependent part, like WE's WorldEditPlugin class

#### BukkitPlugin
An abstract base class for Bukkit FrameworkPlugins, manages things like
passing onLoad/Enable/Disable/Command to the MetaPlugin.  
Could be extended to pass commands directly to a command system.

#### AbstractMetaPlugin
An abstract base class for MetaPlugins, manages the reference to the framework
plugin.  
Could also be extended to pass commands directly to a command system.

	 /          Abstraction layer          \
	+---------------------------------------+--------------+
	|                                       |              |\
	|      MetaPlugin      FrameworkPlugin  |  JavaPlugin  |
	|           |                 |         |       |      |  Bukkit
	|  AbstractMetaPlugin   BukkitPlugin----+-------+      |
	|           |                 |         |              |/
	+-----------+-----------------+---------+--------------+
	|           |                 |         |
	|     YourMainClass   YourBukkitPlugin  |
	|                                       |
	+---------------------------------------+
	 \             Your plugin             /

#### Relationships between the classes
Hierarchy:

* YourMainClass implements MetaPlugin
* YourBukkitPlugin extends BukkitPlugin
* BukkitPlugin implements FrameworkPlugin


Interface contents:

* FrameworkPlugin MetaPlugin.getFrameworkPlugin()
* MetaPlugin FrameworkPlugin.getMetaPlugin()


Typical implementation:

* YourMainClass contains a reference to FrameworkPlugin
* YourBukkitPlugin contains a reference to YourMainClass

* FrameworkPlugin YourMainClass.getFrameworkPlugin()
* YourMainClass YourBukkitPlugin.getMetaPlugin()


Mathematics
-----------

* Location contains a position Vector, a World reference and pitch/yaw values.
* Vector is a 3d vector with x, y and z components.


Players, CommandSenders, Entities
---------------------------------

	CommandSender   Entity
	          \      /
	           \    /
	            \  /
	             \/
	           Player


* CommandSender can be anything sending a command. Players, console, remote access, etc.
* Entity is anything that exists in the world and can be freely placed.
* Player is a player (duh)


World elements
--------------

* World is a reference to a world or dimension.
* BlockState contains at least typeid and data, but can also contain other information about a block.


Server infrastructure
---------------------

* Environment returns references to various singletons and named objects.
* Scheduler schedules task.
* Network sends certain types of packets.


Event system
------------

* Dispatcher deals with event registration.  
  Currently, it only supports bulk registration.

* Event is a generic event containing the most often used fields.  
  Subclasses might contain more properties.

* EventHandler is an annotation that should be used on an event handler method.  
  The type of event is deduced from the method name by default.  
  The default priority is EventPriority.NORMAL.  
  Cancelled events are still passed by default.  
  All of these options can be overridden.

