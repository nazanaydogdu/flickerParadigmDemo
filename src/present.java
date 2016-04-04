/******************************************************
    P r e s e n t . j a v a 
    
    A simple animation applet specially designed to demonstrate
    flicker and one-shot change blindness effects. Timing is not
    research grade, but is close enough for quick pilot ideas, or
    demonstrations.
     
    (c) 2003, Alan Robinson (http://cogsci.ucsd.edu/~aerobins)
    
    Revision 1.1
    
    This program may used for any purpose, provided credit is 
    given to the original author by name, and with a link to the 
    location where the code can be downloaded.
     
******************************************************/

import java.applet.Applet;
import java.awt.*;

public class present extends Applet implements Runnable
{
Image img[]; // images that will be displayed
int delay[]; // how long each image will be displayed
Color ISIcolor; // color of the ISI between frames
int ISIlength[]; // length of each ISI
int frames = 2; //number of images
int stopAt = 100; // set a limit for the number times the images are shown (ISIs included in count)
int sayOffset = 0; // way to make sure text strings don't overlap onscreen.
long startMS;
Thread runner;

/******************************************************
    misc helper functions
******************************************************/

public void say(String str) // quick way to output error messages
 {	getGraphics().drawString(str,10,sayOffset); System.out.println(str); sayOffset += 10;}
    
public int str2int(String str) // silly way to get int from string, but it works
{ return Integer.valueOf(str).intValue();}

public String get(String tag) // wrapper for reading tags from the <applet> .html code
 {
 String str = getParameter(tag);
 if (str == null) say("Could not read HTML parameter " + tag);
 return str;
 }

//Init is called first, but doesn't do much since we don't have access to the screen yet.
public void init()
 { say("loading..."); }

/******************************************************
    start() - init all images and get thread going
******************************************************/
public void start()
{
MediaTracker tracker = new MediaTracker(this); // tool to make sure all images are loaded
//frames = 2; //str2int(get("frames")); // number of "frames" (images) we will show in a loop
say("Loading " + frames + " frames...");

img = new Image[frames]; // set aside space for all frames
delay = new int[frames]; // how long we will show each image

for (int index = 0; index < frames; index++) // load all images 
    {
    int name = index+1; // number for the name of the imgs
    String file = "p"+name+".png"; // the actual name of the image
        
    try {
        say("loading img "+name);
        img[index] = getImage(getCodeBase(), file); // start http process to download image
        tracker.addImage(img[index], 0);  // 0 is just an arbitrary tag
        
        delay[index] = 800;//str2int(get("delay"+name)); // get how long image will be shown.
        }
    catch (Exception e) { say("Cannot load image " + file + ". reason: "  + e);  }
    }

    ISIlength = new int[frames]; // how long to show each ISI
    ISIcolor = new Color(128, // get the color of all ISIs
                         128,
                         128);
                         
    for (int index = 0; index < frames; index++) // load all ISI lengths 
        {
        int name = index+1; // name of the ISI
        ISIlength[0] = 500;//str2int(get("isi" + name));
        ISIlength[1] = 3000;
        }

//if (getParameter("stop-at") != null) // do we limit the number of times though the loop?
//    stopAt = str2int(get("stop-at"));
//else
//    stopAt = -1;    

// now it's time to wait until all images are ready to display
try { tracker.waitForAll(); } 
    catch (Exception e) 
        { say("Could not load all images: " + e ); }

// start the thread
if (runner == null)
    {
    runner = new Thread (this);
    runner.start();
    }
}

/******************************************************
    stop() - kill thread, result of user telling applet to "stop" (rare)
******************************************************/
public void stop()
{
if (runner != null)
    {
    runner.stop();
    runner = null;
    }
}

/******************************************************
    zzz() - wrapper that makes sleeping easy 
******************************************************/
public void zzz(int ms) 
{
try {runner.sleep(ms);}
            catch (Exception e) {say("sleep error!");}
}


/******************************************************
    run() - thread which causes animation
******************************************************/
public void run()
 {
 Graphics g = getGraphics(); // we will draw here
 Rectangle clip = bounds();

 int frame = 0; // frame wraps around, 
 int ticks = 0; // ticks counts up forever
 
 while(true)  
    {
    if (stopAt != -1 && ticks++ >= stopAt) return;
    
    g.drawImage(img[frame],(this.getWidth()-img[frame].getWidth(this))/2, (this.getHeight()-img[frame].getHeight(this))/2,this);
    zzz(delay[frame]);
    
    if (ISIcolor != null && ISIlength[frame] > 0)
        {
        if (stopAt != -1 && ticks++ >= stopAt) return;   
        
        g.setColor(ISIcolor); 
        g.fillRect((this.getWidth()-img[frame].getWidth(this))/2, (this.getHeight()-img[frame].getHeight(this))/2, img[frame].getWidth(this), img[frame].getHeight(this)); 
        
        zzz(ISIlength[frame]);
        }
    
    if (++frame >= frames) // important to wrap when frame-1 = frames
        {
        frame = 0;
        }
    }
 }

public static void main(String[] args) {
	
}
}

	

