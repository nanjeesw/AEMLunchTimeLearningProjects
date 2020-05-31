package com.community.aem.core;

import com.adobe.cq.sightly.WCMUsePojo;

public class HeroTextComponent
extends WCMUsePojo
{
  
     /** The hero text bean. */
    private HeroTextBean heroTextBean = null;
       
    @Override
    public void activate() throws Exception {
           
         heroTextBean = new HeroTextBean();
          
        String heading;  
        String description ; 
         
        //Get the values that the author entered into the AEM dialog
       heading = getProperties().get("jcr:heading", "");
       description = getProperties().get("jcr:description","");
         
        heroTextBean.setHeadingText(heading);
        heroTextBean.setDescription(description); 
      }
       
  public HeroTextBean getHeroTextBean() {
        return this.heroTextBean;
    }
}
