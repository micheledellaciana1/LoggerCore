package LoggerCore.Moira;

import org.knowm.waveforms4j.DWF;
import org.knowm.waveforms4j.DWFException;

public class dwf_extended extends DWF {

    private double _ADCFreq;
    private double _TimeBin;
    private int _AnalogInBufferSize;
    private double[] _voltsRange = { 0, 0 };
    private double[] _actualDCOffsetGen = { 0, 0 };

    public dwf_extended() {
        super();
    }

    public boolean startAnalogCaptureBothChannels(double frequency, int buffersize, double voltageRangeChannel1,
            double voltageRangeChannel2) {

        boolean success = true;
        success = success && FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_1, true);
        success = success && FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_1, voltageRangeChannel1);
        success = success && FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_2, true);
        success = success && FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_2, voltageRangeChannel2);
        success = success && FDwfAnalogInFrequencySet(frequency);
        success = success && FDwfAnalogInBufferSizeSet(buffersize);
        success = success && FDwfAnalogInAcquisitionModeSet(AcquisitionMode.Single.getId());
        success = success && FDwfAnalogInConfigure(true, true);

        if (!success) {
            FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_1, true);
            FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_2, true);
            FDwfAnalogInConfigure(false, false);
            throw new DWFException(FDwfGetLastErrorMsg());
        }

        return true;
    }

    @Override
    public boolean FDwfAnalogInChannelRangeSet(int idxChannel, double voltsRange) {
        _voltsRange[idxChannel] = voltsRange;
        return super.FDwfAnalogInChannelRangeSet(idxChannel, voltsRange);
    }

    @Override
    public boolean FDwfAnalogOutNodeOffsetSet(int idxChannel, double offset) {
        _actualDCOffsetGen[idxChannel] = offset;
        return super.FDwfAnalogOutNodeOffsetSet(idxChannel, offset);
    }

    public boolean setTriggerAnalogIn(int trigChannel, double trigLevel, double trigPosition, int trigCondition,
            double timeoutsec) {
        boolean success = true;
        success = success && FDwfAnalogInTriggerPositionSet(trigPosition);
        success = success && FDwfAnalogInTriggerAutoTimeoutSet(timeoutsec);
        success = success && FDwfAnalogInTriggerSourceSet(DWF.TriggerSource.trigsrcDetectorAnalogIn.getId());

        success = success && FDwfAnalogInTriggerTypeSet(DWF.AnalogTriggerType.trigtypeEdge.getId());
        success = success && FDwfAnalogInTriggerChannelSet(trigChannel);
        success = success && FDwfAnalogInTriggerLevelSet(trigLevel);
        success = success && FDwfAnalogInTriggerConditionSet(trigCondition);
        success = success && FDwfAnalogInConfigure(true, true);

        return success;
    }

    public boolean stopTriggerAnalogIn() {
        boolean success = true;
        success = success && FDwfAnalogInTriggerSourceSet(DWF.TriggerSource.trigsrcNone.getId());
        success = success && FDwfAnalogInConfigure(true, true);
        return success;
    }

    public boolean setTriggerAnalogInAuto(int trigChannel, double timeoutSec, double trigPosition, int trigCondition) {
        boolean success = true;
        if (timeoutSec == 0)
            return false;
        success = success && FDwfAnalogInTriggerPositionSet(trigPosition);
        success = success && FDwfAnalogInTriggerAutoTimeoutSet(timeoutSec);
        success = success && FDwfAnalogInTriggerSourceSet(DWF.TriggerSource.trigsrcDetectorAnalogIn.getId());
        success = success && FDwfAnalogInTriggerTypeSet(DWF.AnalogTriggerType.trigtypeEdge.getId());
        success = success && FDwfAnalogInTriggerChannelSet(trigChannel);
        success = success && FDwfAnalogInTriggerConditionSet(trigCondition);
        success = success && FDwfAnalogInConfigure(true, true);

        return success;
    }

    public boolean startWave(int idxChannel, int waveform, double frequency, double amplitude, double offset,
            double dutyCycle) {

        boolean success = true;
        success = success && FDwfAnalogOutNodeEnableSet(idxChannel, true);
        success = success && FDwfAnalogOutNodeFunctionSet(idxChannel, waveform);
        success = success && FDwfAnalogOutNodeFrequencySet(idxChannel, frequency);
        success = success && FDwfAnalogOutNodeAmplitudeSet(idxChannel, amplitude);
        success = success && FDwfAnalogOutNodeOffsetSet(idxChannel, offset);
        success = success && FDwfAnalogOutNodeSymmetrySet(idxChannel, dutyCycle);
        success = success && FDwfAnalogOutConfigure(idxChannel, true);
        if (!success) {
            FDwfAnalogOutNodeEnableSet(idxChannel, false);
            FDwfAnalogOutConfigure(idxChannel, false);
            throw new DWFException(FDwfGetLastErrorMsg());
        }

        return success;
    }

    @Override
    public boolean FDwfAnalogInFrequencySet(double hzFrequency) {
        _ADCFreq = hzFrequency;
        _TimeBin = 1. / hzFrequency;
        return super.FDwfAnalogInFrequencySet(hzFrequency);
    }

    @Override
    public boolean FDwfAnalogInBufferSizeSet(int size) {
        if (size > DWF.AD2_MAX_BUFFER_SIZE) {
            _AnalogInBufferSize = DWF.AD2_MAX_BUFFER_SIZE;
        }

        _AnalogInBufferSize = size;
        return super.FDwfAnalogInBufferSizeSet(size);
    }

    public enum DigitalState {
        LOW(0), HIGH(1), FLOATING(2);

        private final int id;

        private DigitalState(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

    };

    private int bitMaskEnableOutput = 0b0;

    private int setbitOff(int Mask, int Position) {
        return Mask & (~(0b1 << Position));
    }

    private int setbitOn(int Mask, int Position) {
        return Mask | (0b1 << Position);
    }

    @Override
    public boolean FDwfDigitalIOOutputEnableSet(int outputEnableMask) {
        bitMaskEnableOutput = outputEnableMask;
        return super.FDwfDigitalIOOutputEnableSet(bitMaskEnableOutput);
    }

    public boolean DigitalWrite(int DigitalPin, int state) {
        boolean success = true;

        int bitMaskOutput = getDigitalIOStatus();

        switch (state) {
            case 1:
                bitMaskEnableOutput = setbitOn(bitMaskEnableOutput, DigitalPin);
                bitMaskOutput = setbitOn(bitMaskOutput, DigitalPin);
                break;
            case 0:
                bitMaskEnableOutput = setbitOn(bitMaskEnableOutput, DigitalPin);
                bitMaskOutput = setbitOff(bitMaskOutput, DigitalPin);
                break;
        }

        FDwfDigitalIOOutputEnableSet(bitMaskEnableOutput);
        FDwfDigitalIOOutputSet(bitMaskOutput);
        FDwfDigitalIOConfigure();

        return success;
    }

    public double getADCFreq() {
        return _ADCFreq;
    }

    public double getTimeBin() {
        return _TimeBin;
    }

    public double getVoltageRange(int channel) {
        return _voltsRange[channel];
    }

    public int getAnalogInBufferSize() {
        return _AnalogInBufferSize;
    }

    public double getDCOffset(int channel) {
        return _actualDCOffsetGen[channel];
    }
}
