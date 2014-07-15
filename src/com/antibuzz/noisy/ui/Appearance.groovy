package com.antibuzz.noisy.ui

import org.jvnet.substance.SubstanceLookAndFeel

import javax.swing.JFrame
import java.awt.Toolkit

/**
 * Created by shaner on 7/14/2014.
 */
class Appearance {
    static def themes = [
        'Autumn' : 'org.jvnet.substance.skin.AutumnSkin',
        'Black Steel' : 'org.jvnet.substance.skin.BusinessBlackSteelSkin',
        'Blue Steel' : 'org.jvnet.substance.skin.BusinessBlueSteelSkin',
        'Business' : 'org.jvnet.substance.skin.BusinessSkin',
        'Challenger Deep' : 'org.jvnet.substance.skin.ChallengerDeepSkin',
        'Creme Coffee' : 'org.jvnet.substance.skin.CremeCoffeeSkin',
        'Dust Coffee' : 'org.jvnet.substance.skin.DustCoffeeSkin',
        'Dust' : 'org.jvnet.substance.skin.DustSkin',
        'Emerald Dusk' : 'org.jvnet.substance.skin.EmeraldDuskSkin',
        'Magma' : 'org.jvnet.substance.skin.MagmaSkin',
        'Mist Aqua' : 'org.jvnet.substance.skin.MistAquaSkin',
        'Mist Silver' : 'org.jvnet.substance.skin.MistSilverSkin',
        'Moderate' : 'org.jvnet.substance.skin.ModerateSkin',
        'Nebula Brick Wall' : 'org.jvnet.substance.skin.NebulaBrickWallSkin',
        'Nebula' : 'org.jvnet.substance.skin.NebulaSkin',
        'Office 2007 Blue' : 'org.jvnet.substance.skin.OfficeBlue2007Skin',
        'Office 2007 Silver' : 'org.jvnet.substance.skin.OfficeSilver2007Skin',
        'Raven Graphite Glass' : 'org.jvnet.substance.skin.RavenGraphiteGlassSkin',
        'Raven Graphite' : 'org.jvnet.substance.skin.RavenGraphiteSkin',
        'Raven' : 'org.jvnet.substance.skin.RavenSkin',
        'Sahara' : 'org.jvnet.substance.skin.SaharaSkin',
        'Twilight' : 'org.jvnet.substance.skin.TwilightSkin'
    ]


    static def setTheme(String name) {
        JFrame.setDefaultLookAndFeelDecorated(true)
        SubstanceLookAndFeel.setSkin(themes[name])
        Toolkit.getDefaultToolkit().setDynamicLayout(true)
    }
}
