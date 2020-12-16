package xyz.nkomarn.harbor.task;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.util.Config;

public class TimeAlter extends BukkitRunnable {

    private final Harbor harbor;
    private final World world;
    private double trueTime;
    private boolean afterSleep;

    private final boolean enabled;
    private final int waitTick;
    
    public TimeAlter(@NotNull Harbor harbor, @NotNull World world) {
        this.harbor = harbor;
        this.world = world;
        this.trueTime = world.getTime();
        this.afterSleep = false;
        Config config = harbor.getConfiguration();
        this.enabled = config.getBoolean("speed.enabled");
        this.waitTick = Math.min(100, Math.max(1, config.getInteger("speed.update-every")));
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, !this.enabled);
        runTaskTimer(harbor, this.waitTick, this.waitTick);
    }

    @Override
    public void run() {
        Config config = harbor.getConfiguration();
        
        if (enabled) {
        	if (!harbor.skippingNight) {
		        long time = world.getTime();
		        double daySpeed = Math.min(10.0D, Math.max(0.1D, config.getDouble("speed.day")));
		        double nightSpeed = Math.min(10.0D, Math.max(0.1D, config.getDouble("speed.night")));
		        double speed = daySpeed;
		        if (time > 12000) {
		        	speed = nightSpeed;
		        }
		        if (afterSleep) {
		        	trueTime = time;
		        }
		        double addTime = waitTick * speed;
		        if (Math.abs(time - trueTime) > Math.max(waitTick, addTime * 4)) {
		        	trueTime = time;
		        }
		        trueTime += waitTick * speed;
		        world.setTime((long) Math.floor(trueTime));
        	}
	        
	        
	        if (afterSleep) {
		        if (!harbor.skippingNight) {
		        	afterSleep = false;
		        }
	        } else {
		        if (harbor.skippingNight) {
		        	afterSleep = true;
		        }
	        }
        }
    }
}
