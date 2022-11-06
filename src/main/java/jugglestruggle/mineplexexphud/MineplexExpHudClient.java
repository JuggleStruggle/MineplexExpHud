/*
 * MineplexExpHud: A mod which tracks the current
 * EXP the user has on the Mineplex server.
 * Copyright (C) 2022  JuggleStruggle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 *  <https://www.gnu.org/licenses/>.
 */

package jugglestruggle.mineplexexphud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import jugglestruggle.mineplexexphud.hud.RenderContext;
import jugglestruggle.mineplexexphud.hud.info.ExpCacheState;
import jugglestruggle.mineplexexphud.pref.Preferences;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public abstract class MineplexExpHudClient
{
    public static final String LANG_FORMAT = "jugglestruggle.miplexp.";
    public static final Logger LOGGER = LogManager.getLogger();
    
    protected static MineplexExpHudClient instance;
    protected static Lang langForTranslation;
    protected static RenderContext renderContext;
    
    public static MineplexExpHudClient getInstance() {
        return MineplexExpHudClient.instance;
    }
    
    
    public static Lang getLang() {
        return MineplexExpHudClient.langForTranslation;
    }
    public static RenderContext getRenderContext() {
        return MineplexExpHudClient.renderContext;
    }
    
    
    protected File preferencesFile;
    protected Gson gson;
    
    protected AbstractExpHud expHud;
    
    public ExpCacheState tempExpCacheReadFromConfig;
    
    protected void init(File configDirectory)
    {
        this.preferencesFile = new File(configDirectory, "MineplexExpHud.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        this.readFromFile();
    }
    
    public boolean readFromFile()
    {
        boolean createConfig = false;
        
        if (this.preferencesFile.exists())
        {
            FileReader reader = null;
            
            try
            {
                reader = new FileReader(this.preferencesFile);
                JsonElement data = (new JsonParser()).parse(reader);
                
                if (data != null && data.isJsonObject())
                {
                    JsonObject obj = data.getAsJsonObject();
                    
                    if (Preferences.read(obj))
                    {
                        // Attempt to read the cached exp if it has any
                        if (obj.has("expStatus"))
                            this.tempExpCacheReadFromConfig = ExpCacheState.readFromElem(obj.get("expStatus"));
                            
                        return true;
                    }
                    else
                        createConfig = true;
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                createConfig = true;
            }
            finally {
                IOUtils.closeQuietly(reader);
            }
            
        }
        else {
            createConfig = true;
        }
        
        if (createConfig)
        {
            if (this.preferencesFile.exists())
                this.preferencesFile.delete();
            
            this.writeToFile();
        }
    
        return false;
    }
    public boolean writeToFile()
    {
        JsonObject prefData = new JsonObject();
        
        if (Preferences.write(prefData))
        {
            final AbstractExpHud expHud = this.getExpHud();
            
            // Store the current exp the user had if it exists (which it will) and is not empty
            // a.k.a. all values not being zero
            if (expHud != null && expHud.expStatus != null && !expHud.expStatus.isEmpty())
                prefData.add("expStatus", expHud.expStatus.write());
    
            FileWriter writer = null;
            JsonWriter jWriter = null;
    
            try
            {
                if (!this.preferencesFile.exists())
                    this.preferencesFile.createNewFile();
    
                writer = new FileWriter(this.preferencesFile);
                jWriter = new JsonWriter(writer);
    
                this.gson.toJson(prefData, jWriter);
        
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
            finally
            {
                IOUtils.closeQuietly(jWriter);
                IOUtils.closeQuietly(writer);
            }
        }
    
        return false;
    }
    
    public AbstractExpHud getExpHud() {
        return this.expHud;
    }
}
