package net.ctlserv.hecate.config;

import lombok.Getter;
import lombok.Setter;
import net.ctlserv.hecate.board.HecateBoardProvider;

/**
 * Created by Williambraecky on 25-05-17.
 */
@Getter
@Setter
public class HecateConfiguration {

    boolean useTab = false;
    long defaultRefreshRate = 2L;
    HecateBoardProvider defaultProvider = null;


}
