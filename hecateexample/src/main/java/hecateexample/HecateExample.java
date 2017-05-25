package hecateexample;

import net.ctlserv.hecate.Hecate;
import net.ctlserv.hecate.config.HecateConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Williambraecky on 25-05-17.
 */
public class HecateExample extends JavaPlugin {

    public void onEnable() {
        HecateConfiguration hecateConfiguration = new HecateConfiguration();
        hecateConfiguration.setUseTab(true);
        hecateConfiguration.setDefaultProvider(new HecateExampleProvider(this));
        hecateConfiguration.setDefaultRefreshRate(20L);
        new Hecate(this, hecateConfiguration);
    }
}
