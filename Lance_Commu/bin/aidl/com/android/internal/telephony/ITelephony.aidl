package com.android.internal.telephony;

import android.os.Bundle;
import java.util.List;

    interface ITelephony {

 

        /**

         * Dial a number. This doesn't place the call. It displays

         * the Dialer screen.

         * @param number the number to be dialed. If null, this

         * would display the Dialer screen with no number pre-filled.

         */

        void dial(String number);

 

        /**

         * Place a call to the specified number.

         * @param number the number to be called.

         */

        void call(String number);

 

        /**

         * If there is currently a call in progress, show the call screen.

         * The DTMF dialpad may or may not be visible initially, depending on

         * whether it was up when the user last exited the InCallScreen.

         *

         * @return true if the call screen was shown.

         */

        boolean showCallScreen();

 

        /**

         * Variation of showCallScreen() that also specifies whether the

         * DTMF dialpad should be initially visible when the InCallScreen

         * comes up.

         *

         * @param showDialpad if true, make the dialpad visible initially,

         *                    otherwise hide the dialpad initially.

         * @return true if the call screen was shown.

         *

         * @see showCallScreen

         */

        boolean showCallScreenWithDialpad(boolean showDialpad);

 

        /**

         * End call or go to the Home screen

         *

         * @return whether it hung up

         */

        boolean endCall();
        
        
        boolean answerRingingCall();
        
        void silenceRinger();

    }


