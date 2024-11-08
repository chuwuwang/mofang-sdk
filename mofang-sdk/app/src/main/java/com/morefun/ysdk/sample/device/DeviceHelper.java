package com.morefun.ysdk.sample.device;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.morefun.yapi.card.at24cxx.IAT24CxxCard;
import com.morefun.yapi.card.cpu.CPUCardHandler;
import com.morefun.yapi.card.cpu.CPUTypeAHandler;
import com.morefun.yapi.card.emulate.EmulateCardHandler;
import com.morefun.yapi.card.industry.IndustryCardHandler;
import com.morefun.yapi.card.industry.NTagCard;
import com.morefun.yapi.card.industry.SL4442Card;
import com.morefun.yapi.card.mifare.M0CHandler;
import com.morefun.yapi.card.mifare.M0Ev1Handler;
import com.morefun.yapi.card.mifare.M1CardHandler;
import com.morefun.yapi.device.beeper.Beeper;
import com.morefun.yapi.device.dukpt.IDukpt;
import com.morefun.yapi.device.hsm.Hsm;
import com.morefun.yapi.device.led.LEDDriver;
import com.morefun.yapi.device.logrecorder.LogRecorder;
import com.morefun.yapi.device.ped.IPed;
import com.morefun.yapi.device.pinpad.PinPad;
import com.morefun.yapi.device.pinpad.SecureArea;
import com.morefun.yapi.device.printer.MultipleAppPrinter;
import com.morefun.yapi.device.printer.Printer;
import com.morefun.yapi.device.reader.icc.IccCardReader;
import com.morefun.yapi.device.reader.mag.MagCardReader;
import com.morefun.yapi.device.scanner.InnerScanner;
import com.morefun.yapi.device.serialport.SerialPort;
import com.morefun.yapi.device.serialport.SerialPortDriver;
import com.morefun.yapi.device.tpm.TpmManager;
import com.morefun.yapi.emv.EmvHandler;
import com.morefun.yapi.emv.EmvRupayService;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.morefun.ysdk.sample.MyApplication;
import com.morefun.ysdk.sample.utils.ToastUtils;


public class DeviceHelper {

    private final static String TAG = DeviceHelper.class.getName();

    private static PinPad pinpad;
    private static IccCardReader iccCardReader;
    private static CPUCardHandler cpuCardHandler;
    private static M1CardHandler m1CardHandler;

    private static M0Ev1Handler m0Ev1Handler;
    private static M0CHandler m0CHandler;
    private static SL4442Card sl4442Card;
    private static NTagCard nTagCard;
    private static SecureArea mSecureArea;
    private static Hsm mHsm;
    private static IndustryCardHandler industryCardHandler;
    private static MagCardReader magCardReader;
    private static LEDDriver ledDriver;
    private static MultipleAppPrinter printer;
    private static SerialPortDriver serialPortDriver;
    private static Beeper beeper;
    private static EmvHandler emvHandler;
    private static InnerScanner innerScanner;
    private static LogRecorder logRecorder;
    private static EmvRupayService emvRupayService;
    private static Printer printer1;
    private static IPed ped;
    private static CPUTypeAHandler cpuTypeAHandler;
    private static EmulateCardHandler emulateCardHandler;
    private static MyApplication application;
    private static SerialPort usbSerialPort;
    private static IDukpt dukpt;
    private static TpmManager tpmManager;
    private static IAT24CxxCard at24CxxCard;

    @SuppressLint("NewApi")
    public static void initDevices(MyApplication application) throws RemoteException {
        DeviceHelper.application = application;

        if (application == null) {
            return;
        }

        if (application.getDeviceService() != null) {
            try {
                pinpad = getPinpad();
                magCardReader = getMagCardReader();
                ledDriver = getLedDriver();
                printer = getPrinter();
                beeper = getBeeper();
                emvHandler = getEmvHandler();
                innerScanner = getInnerScanner();
                logRecorder = getLogRecorder();
                emvRupayService = getEmvRupayService();
                cpuTypeAHandler = getCpuTypeAHandler();
                emulateCardHandler = getEmulateCardHandler();
                dukpt = getDukpt();
                tpmManager = getTpmManager();
                at24CxxCard = getAT24CxxCard();
            } catch (RemoteException e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            application.bindDeviceService();
            reset();
        }
    }

    public static DeviceServiceEngine getDeviceService() throws RemoteException {
        Log.d(TAG, "application:" + application);
        if (application == null) {
            ToastUtils.show(MyApplication.getInstance(), "application is null, please try again later.");
            throw new RemoteException("application is null, please try again later.");
        }

        if (application.getDeviceService() == null) {
            checkState();
        }

        return application.getDeviceService();
    }

    public static void checkState() throws RemoteException {
        if (application == null) {
            Log.d(TAG, "============checkState application == null");
            throw new RemoteException("Please restart the application.");
        }

        if (application.getDeviceService() == null) {
            application.bindDeviceService();
            reset();
            throw new RemoteException("Device service connection failed, please try again later.");
        }
    }


    @SuppressLint("NewApi")
    public static PinPad getPinpad() throws RemoteException {
        if (pinpad == null) {
            checkState();
            try {
                return application.getDeviceService().getPinPad();
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return pinpad;
        }
    }

    @SuppressLint("NewApi")
    public static IPed getPed() throws RemoteException {
        if (ped == null) {
            checkState();
            try {
                return application.getDeviceService().getPed();
            } catch (RemoteException e) {
                throw new RemoteException("Ped service acquisition failed, please try again later.");
            }
        } else {
            return ped;
        }
    }


    @SuppressLint("NewApi")
    public static IccCardReader getIccCardReader(int cardType) throws RemoteException {
        if (iccCardReader == null) {
            checkState();
            try {
                return application.getDeviceService().getIccCardReader(cardType);
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return iccCardReader;
        }
    }


    @SuppressLint("NewApi")
    public static CPUCardHandler getCpuCardHandler(IccCardReader iccCardReader) throws RemoteException {
        if (cpuCardHandler == null) {
            checkState();
            try {
                return application.getDeviceService().getCPUCardHandler(iccCardReader);
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return cpuCardHandler;
        }
    }

    @SuppressLint("NewApi")
    public static M1CardHandler getM1CardHandler(IccCardReader iccCardReader) throws RemoteException {
        if (m1CardHandler == null) {
            checkState();
            try {
                return application.getDeviceService().getM1CardHandler(iccCardReader);
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return m1CardHandler;
        }
    }

    @SuppressLint("NewApi")
    public static M0Ev1Handler getM0Ev1CardHandler(IccCardReader iccCardReader) throws RemoteException {
        if (m0Ev1Handler == null) {
            checkState();
            try {
                return application.getDeviceService().getM0Ev1CardHandler(iccCardReader);
            } catch (RemoteException e) {
                throw new RemoteException("m0Ev1Handler service acquisition failed, please try again later.");
            }
        } else {
            return m0Ev1Handler;
        }
    }

    @SuppressLint("NewApi")
    public static M0CHandler getM0CCardHandler(IccCardReader iccCardReader) throws RemoteException {
        if (m0CHandler == null) {
            checkState();
            try {
                return application.getDeviceService().getM0CCardHandler(null);
            } catch (RemoteException e) {
                throw new RemoteException("m0CHandler service acquisition failed, please try again later.");
            }
        } else {
            return m0CHandler;
        }
    }

    @SuppressLint("NewApi")
    public static NTagCard getNTagCard(IccCardReader iccCardReader) throws RemoteException {
        if (m0CHandler == null) {
            checkState();
            try {
                return application.getDeviceService().getNTagCardHandler(null);
            } catch (RemoteException e) {
                throw new RemoteException("nTagCard service acquisition failed, please try again later.");
            }
        } else {
            return nTagCard;
        }
    }

    public static SL4442Card getSL4442Card(IccCardReader iccCardReader) throws RemoteException {
        if (sl4442Card == null) {
            checkState();
            try {
                return application.getDeviceService().getSL4442CardHandler(null);
            } catch (RemoteException e) {
                throw new RemoteException("sl4442 service acquisition failed, please try again later.");
            }
        } else {
            return sl4442Card;
        }
    }

    @SuppressLint("NewApi")
    public static SecureArea getSecureArea() throws RemoteException {
        if (mSecureArea == null) {
            checkState();
            try {
                return application.getDeviceService().getSecureAreaHandler();
            } catch (RemoteException e) {
                throw new RemoteException("SecureArea service acquisition failed, please try again later.");
            }
        } else {
            return mSecureArea;
        }
    }

    @SuppressLint("NewApi")
    public static Hsm getHsm() throws RemoteException {
        if (mHsm == null) {
            checkState();
            try {
                return application.getDeviceService().getHsmDevice();
            } catch (RemoteException e) {
                throw new RemoteException("SecureArea service acquisition failed, please try again later.");
            }
        } else {
            return mHsm;
        }
    }


    @SuppressLint("NewApi")
    public static IndustryCardHandler getIndustryCardHandler(IccCardReader iccCardReader) throws RemoteException {
        if (industryCardHandler == null) {
            checkState();
            try {
                return application.getDeviceService().getIndustryCardHandler(iccCardReader);
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return industryCardHandler;
        }
    }

    @SuppressLint("NewApi")
    public static MagCardReader getMagCardReader() throws RemoteException {
        if (magCardReader == null) {
            checkState();
            try {
                return application.getDeviceService().getMagCardReader();
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return magCardReader;
        }
    }

    @SuppressLint("NewApi")
    public static LEDDriver getLedDriver() throws RemoteException {
        if (ledDriver == null) {
            checkState();
            try {
                return application.getDeviceService().getLEDDriver();
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return ledDriver;
        }
    }

    @SuppressLint("NewApi")
    public static MultipleAppPrinter getPrinter() throws RemoteException {
        if (printer == null) {
            checkState();
            try {
                return application.getDeviceService().getMultipleAppPrinter();
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return printer;
        }
    }

    @SuppressLint("NewApi")
    public static SerialPortDriver getSerialPortDriver(int port) throws RemoteException {
        if (serialPortDriver == null) {
            checkState();
            try {
                return application.getDeviceService().getSerialPortDriver(port);
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return serialPortDriver;
        }
    }

    @SuppressLint("NewApi")
    public static SerialPort getUsbSerialPort(String path) throws RemoteException {
        if (usbSerialPort == null) {
            checkState();
            try {
                return application.getDeviceService().getSerialPort(path);
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return usbSerialPort;
        }
    }

    @SuppressLint("NewApi")
    public static Beeper getBeeper() throws RemoteException {
        if (beeper == null) {
            checkState();
            try {
                return application.getDeviceService().getBeeper();
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return beeper;
        }
    }

    @SuppressLint("NewApi")
    public static EmvHandler getEmvHandler() throws RemoteException {
        if (emvHandler == null) {
            checkState();
            try {
                return application.getDeviceService().getEmvHandler();
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return emvHandler;
        }
    }

    @SuppressLint("NewApi")
    public static EmvRupayService getEmvRupayService() throws RemoteException {
        if (emvRupayService == null) {
            checkState();
            try {
                return application.getDeviceService().getEmvRupayService();
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return emvRupayService;
        }
    }

    @SuppressLint("NewApi")
    public static InnerScanner getInnerScanner() throws RemoteException {
        if (innerScanner == null) {
            checkState();
            try {
                return application.getDeviceService().getInnerScanner();
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return innerScanner;
        }
    }

    @SuppressLint("NewApi")
    public static LogRecorder getLogRecorder() throws RemoteException {
        if (logRecorder == null) {
            checkState();
            try {
                return application.getDeviceService().getLogRecorder();
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return logRecorder;
        }
    }

    @SuppressLint("NewApi")
    public static CPUTypeAHandler getCpuTypeAHandler() throws RemoteException {
        if (cpuTypeAHandler == null) {
            checkState();
            try {
                return application.getDeviceService().getCPUTypeAHandler();
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return cpuTypeAHandler;
        }
    }

    @SuppressLint("NewApi")
    public static EmulateCardHandler getEmulateCardHandler() throws RemoteException {
        if (emulateCardHandler == null) {
            checkState();
            try {
                return application.getDeviceService().getEmulateCardHandler();
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return emulateCardHandler;
        }
    }

    @SuppressLint("NewApi")
    public static IDukpt getDukpt() throws RemoteException {
        if (dukpt == null) {
            checkState();
            try {
                return application.getDeviceService().getDukpt();
            } catch (RemoteException e) {
                throw new RemoteException("PinPad service acquisition failed, please try again later.");
            }
        } else {
            return dukpt;
        }
    }

    @SuppressLint("NewApi")
    public static TpmManager getTpmManager() throws RemoteException {
        if (tpmManager == null) {
            checkState();
            try {
                return application.getDeviceService().getTpmManager();
            } catch (RemoteException e) {
                throw new RemoteException("Tpm service acquisition failed, please try again later.");
            }
        } else {
            return tpmManager;
        }
    }

    @SuppressLint("NewApi")
    public static IAT24CxxCard getAT24CxxCard() throws RemoteException {
        if (at24CxxCard == null) {
            checkState();
            try {
                return application.getDeviceService().getAT24CxxCard();
            } catch (RemoteException e) {
                throw new RemoteException("Tpm service acquisition failed, please try again later.");
            }
        } else {
            return at24CxxCard;
        }
    }


    public static void reset() {
        pinpad = null;
        iccCardReader = null;
        cpuCardHandler = null;
        m1CardHandler = null;
        industryCardHandler = null;
        magCardReader = null;
        ledDriver = null;
        printer = null;
        serialPortDriver = null;
        beeper = null;
        emvHandler = null;
        innerScanner = null;
        logRecorder = null;
        ped = null;
        emulateCardHandler = null;
        usbSerialPort = null;
        dukpt = null;
        tpmManager = null;
        at24CxxCard = null;
    }

}
