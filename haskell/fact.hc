/* GHC_PACKAGES base integer-gmp ghc-prim rts ffi-1.0
*/
#include "Stg.h"
EI_(integerzmgmp_GHCziInteger_smallInteger_closure);
static StgWord rd9_srt[] = {
(W_)&integerzmgmp_GHCziInteger_smallInteger_closure
};

II_(rd9_info);
static StgWord rd9_closure[] = {
(W_)&rd9_info, 0x0
};

static StgWord skb_info[] = {
((W_)&rd9_srt+0), 0x0, 0x10010U
};

EI_(integerzmgmp_GHCziInteger_smallInteger_closure);
IF_(skb_entry) {
FB_
if ((W_)(((W_)Sp - 0xcU) < (W_)SpLim)) goto _ckt;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = R1.w;
R1.w = (W_)&integerzmgmp_GHCziInteger_smallInteger_closure;
Sp[-3] = 0x1U;
Sp=Sp-3;
JMP_((W_)&stg_ap_n_fast);
_ckt:
JMP_(stg_gc_enter_1);
FE_
}

static StgWord sjW_info[] = {
((W_)&rd9_srt+0), 0x1U, 0x10011U
};

EI_(base_GHCziNum_fromInteger_info);
II_(skb_info);
IF_(sjW_entry) {
FB_
if ((W_)(((W_)Sp - 0x14U) < (W_)SpLim)) goto _ckw;
Hp=Hp+2;
if ((W_)((W_)Hp > (W_)HpLim)) goto _cky;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = R1.w;
Hp[-1] = (W_)&skb_info;
Sp[-3] = (W_)Hp-4;
Sp[-4] = (W_)&stg_ap_p_info;
Sp[-5] = R1.p[2];
Sp=Sp-5;
JMP_((W_)&base_GHCziNum_fromInteger_info);
_ckw:
JMP_(stg_gc_enter_1);
_cky:
HpAlloc = 0x8U;
goto _ckw;
FE_
}

static StgWord skc_info[] = {
((W_)&rd9_srt+0), 0x0, 0x10010U
};

EI_(integerzmgmp_GHCziInteger_smallInteger_closure);
IF_(skc_entry) {
FB_
if ((W_)(((W_)Sp - 0xcU) < (W_)SpLim)) goto _ckF;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = R1.w;
R1.w = (W_)&integerzmgmp_GHCziInteger_smallInteger_closure;
Sp[-3] = 0x0;
Sp=Sp-3;
JMP_((W_)&stg_ap_n_fast);
_ckF:
JMP_(stg_gc_enter_1);
FE_
}

static StgWord sjZ_info[] = {
((W_)&rd9_srt+0), 0x1U, 0x10011U
};

EI_(base_GHCziNum_fromInteger_info);
II_(skc_info);
IF_(sjZ_entry) {
FB_
if ((W_)(((W_)Sp - 0x14U) < (W_)SpLim)) goto _ckI;
Hp=Hp+2;
if ((W_)((W_)Hp > (W_)HpLim)) goto _ckK;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = R1.w;
Hp[-1] = (W_)&skc_info;
Sp[-3] = (W_)Hp-4;
Sp[-4] = (W_)&stg_ap_p_info;
Sp[-5] = R1.p[2];
Sp=Sp-5;
JMP_((W_)&base_GHCziNum_fromInteger_info);
_ckI:
JMP_(stg_gc_enter_1);
_ckK:
HpAlloc = 0x8U;
goto _ckI;
FE_
}

static StgWord skf_info[] = {
0x3U, 0x10U
};

EI_(base_GHCziNum_zm_info);
IF_(skf_entry) {
FB_
if ((W_)(((W_)Sp - 0x18U) < (W_)SpLim)) goto _cl2;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = R1.w;
Sp[-3] = R1.p[3];
Sp[-4] = R1.p[4];
Sp[-5] = (W_)&stg_ap_pp_info;
Sp[-6] = R1.p[2];
Sp=Sp-6;
JMP_((W_)&base_GHCziNum_zm_info);
_cl2:
JMP_(stg_gc_enter_1);
FE_
}

static StgWord skg_info[] = {
0x4U, 0x10U
};

II_(sk5_info);
II_(skf_info);
IF_(skg_entry) {
FB_
if ((W_)(((W_)Sp - 0xcU) < (W_)SpLim)) goto _cl5;
Hp=Hp+5;
if ((W_)((W_)Hp > (W_)HpLim)) goto _cl7;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = R1.w;
Hp[-4] = (W_)&skf_info;
Hp[-2] = R1.p[2];
Hp[-1] = R1.p[3];
*Hp = R1.p[4];
R1.w = R1.p[5];
Sp[-3] = (W_)Hp-16;
Sp=Sp-3;
JMP_((W_)&sk5_info);
_cl5:
JMP_(stg_gc_enter_1);
_cl7:
HpAlloc = 0x14U;
goto _cl5;
FE_
}

static StgWord ske_info[] = {
0x45U, 0x22U
};

EI_(base_GHCziNum_zt_info);
II_(skg_info);
IF_(ske_ret) {
W_ _cla;
FB_
_cla = R1.w & 0x3U;
if ((W_)(_cla >= 0x2U)) goto _clc;
Hp=Hp+6;
if ((W_)((W_)Hp > (W_)HpLim)) goto _clf;
Hp[-5] = (W_)&skg_info;
Hp[-3] = Sp[4];
Hp[-2] = Sp[3];
Hp[-1] = Sp[5];
*Hp = Sp[1];
Sp[3] = (W_)&stg_ap_pp_info;
Sp[2] = Sp[4];
Sp[4] = Sp[5];
Sp[5] = (W_)Hp-20;
Sp=Sp+2;
JMP_((W_)&base_GHCziNum_zt_info);
_clc:
R1.w = Sp[3];
Sp=Sp+6;
JMP_((W_)&stg_ap_0_fast);
_clg:
JMP_(stg_gc_enter_1);
_clf:
HpAlloc = 0x18U;
goto _clg;
FE_
}

static StgWord skd_info[] = {
0x5U, 0x22U
};

EI_(base_GHCziClasses_zeze_info);
II_(ske_info);
IF_(skd_ret) {
FB_
Sp[-1] = Sp[2];
Sp[-2] = Sp[5];
Sp[-3] = (W_)&stg_ap_pp_info;
Sp[-4] = R1.w;
*Sp = (W_)&ske_info;
Sp=Sp-4;
JMP_((W_)&base_GHCziClasses_zeze_info);
FE_
}

static StgWord sk5_info[] = {
0x10005U, 0x3U, 0x9U
};

EI_(base_GHCziNum_zdp1Num_info);
II_(skd_info);
IF_(sk5_entry) {
FB_
if ((W_)(((W_)Sp - 0x24U) < (W_)SpLim)) goto _clk;
Sp[-4] = R1.w;
Sp[-3] = *((P_)(R1.w+11));
Sp[-2] = *((P_)(R1.w+7));
Sp[-1] = *((P_)(R1.w+3));
Sp[-6] = *((P_)(R1.w+3));
Sp[-5] = (W_)&skd_info;
Sp=Sp-6;
JMP_((W_)&base_GHCziNum_zdp1Num_info);
_clk:
JMP_(stg_gc_fun);
FE_
}

static StgWord rd9_info[] = {
((W_)&rd9_srt+0), 0x10005U, 0x0, 0x1000fU
};

II_(rd9_closure);
II_(sjW_info);
II_(sjZ_info);
II_(sk5_info);
IF_(rd9_entry) {
FB_
Hp=Hp+10;
if ((W_)((W_)Hp > (W_)HpLim)) goto _clo;
Hp[-9] = (W_)&sjW_info;
Hp[-7] = *Sp;
Hp[-6] = (W_)&sjZ_info;
Hp[-4] = *Sp;
Hp[-3] = (W_)&sk5_info;
Hp[-2] = *Sp;
Hp[-1] = (W_)Hp-36;
*Hp = (W_)Hp-24;
R1.w = (W_)Hp-11;
Sp=Sp+1;
JMP_(*Sp);
_clp:
R1.w = (W_)&rd9_closure;
JMP_(stg_gc_fun);
_clo:
HpAlloc = 0x28U;
goto _clp;
FE_
}
EI_(base_GHCziNum_zdfNumInteger_closure);
static StgWord rjM_srt[] = {
(W_)&base_GHCziNum_zdfNumInteger_closure
};

II_(rjM_info);
static StgWord rjM_closure[] = {
(W_)&rjM_info, 0x0, 0x0, 0x0
};

static StgWord rjM_info[] = {
((W_)&rjM_srt+0), 0x0, 0x10016U
};

EI_(base_GHCziNum_zdfNumInteger_closure);
EI_(base_GHCziNum_zdp2Num_info);
IF_(rjM_entry) {
FB_
if ((W_)(((W_)Sp - 0xcU) < (W_)SpLim)) goto _clz;
Hp=Hp+2;
if ((W_)((W_)Hp > (W_)HpLim)) goto _clB;
Hp[-1] = (W_)&stg_CAF_BLACKHOLE_info;
;EF_(newCAF);
{void (*ghcFunPtr)(void *);
ghcFunPtr = ((void (*)(void *))(W_)&newCAF);
ghcFunPtr((void *)R1.w);;}
R1.p[1] = (W_)Hp-4;
*R1.p = (W_)&stg_IND_STATIC_info;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = (W_)Hp-4;
Sp[-3] = (W_)&base_GHCziNum_zdfNumInteger_closure;
Sp=Sp-3;
JMP_((W_)&base_GHCziNum_zdp2Num_info);
_clz:
JMP_(stg_gc_enter_1);
_clB:
HpAlloc = 0x8U;
goto _clz;
FE_
}
EI_(base_SystemziIO_print_closure);
II_(rjM_closure);
static StgWord rjO_srt[] = {
(W_)&base_SystemziIO_print_closure, (W_)&rjM_closure
};

II_(rjO_info);
static StgWord rjO_closure[] = {
(W_)&rjO_info, 0x0, 0x0, 0x0
};

static StgWord rjO_info[] = {
((W_)&rjO_srt+0), 0x0, 0x30016U
};

EI_(base_SystemziIO_print_closure);
II_(rjM_closure);
IF_(rjO_entry) {
FB_
if ((W_)(((W_)Sp - 0xcU) < (W_)SpLim)) goto _clL;
Hp=Hp+2;
if ((W_)((W_)Hp > (W_)HpLim)) goto _clN;
Hp[-1] = (W_)&stg_CAF_BLACKHOLE_info;
;EF_(newCAF);
{void (*ghcFunPtr)(void *);
ghcFunPtr = ((void (*)(void *))(W_)&newCAF);
ghcFunPtr((void *)R1.w);;}
R1.p[1] = (W_)Hp-4;
*R1.p = (W_)&stg_IND_STATIC_info;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = (W_)Hp-4;
R1.w = (W_)&base_SystemziIO_print_closure;
Sp[-3] = (W_)&rjM_closure;
Sp=Sp-3;
JMP_((W_)&stg_ap_p_fast);
_clL:
JMP_(stg_gc_enter_1);
_clN:
HpAlloc = 0x8U;
goto _clL;
FE_
}
EI_(integerzmgmp_GHCziInteger_smallInteger_closure);
EI_(base_GHCziNum_zdfNumInteger_closure);
static StgWord rjQ_srt[] = {
(W_)&base_GHCziNum_zdfNumInteger_closure, (W_)&integerzmgmp_GHCziInteger_smallInteger_closure
};

II_(rjQ_info);
static StgWord rjQ_closure[] = {
(W_)&rjQ_info, 0x0, 0x0, 0x0
};

static StgWord rjQ_info[] = {
((W_)&rjQ_srt+0), 0x0, 0x30016U
};

EI_(base_GHCziNum_zdfNumInteger_closure);
II_(rd9_info);
IF_(rjQ_entry) {
FB_
if ((W_)(((W_)Sp - 0xcU) < (W_)SpLim)) goto _clX;
Hp=Hp+2;
if ((W_)((W_)Hp > (W_)HpLim)) goto _clZ;
Hp[-1] = (W_)&stg_CAF_BLACKHOLE_info;
;EF_(newCAF);
{void (*ghcFunPtr)(void *);
ghcFunPtr = ((void (*)(void *))(W_)&newCAF);
ghcFunPtr((void *)R1.w);;}
R1.p[1] = (W_)Hp-4;
*R1.p = (W_)&stg_IND_STATIC_info;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = (W_)Hp-4;
Sp[-3] = (W_)&base_GHCziNum_zdfNumInteger_closure;
Sp=Sp-3;
JMP_((W_)&rd9_info);
_clX:
JMP_(stg_gc_enter_1);
_clZ:
HpAlloc = 0x8U;
goto _clX;
FE_
}
EI_(integerzmgmp_GHCziInteger_smallInteger_closure);
II_(rjQ_closure);
static StgWord sm3_srt[] = {
(W_)&integerzmgmp_GHCziInteger_smallInteger_closure, (W_)&rjQ_closure
};

II_(sm3_info);
static StgWord sm3_closure[] = {
(W_)&sm3_info, 0x0, 0x0, 0x0
};

static StgWord sm2_info[] = {
((W_)&sm3_srt+0), 0x0, 0x10010U
};

EI_(integerzmgmp_GHCziInteger_smallInteger_closure);
IF_(sm2_entry) {
FB_
if ((W_)(((W_)Sp - 0xcU) < (W_)SpLim)) goto _cmd;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = R1.w;
R1.w = (W_)&integerzmgmp_GHCziInteger_smallInteger_closure;
Sp[-3] = 0x3U;
Sp=Sp-3;
JMP_((W_)&stg_ap_n_fast);
_cmd:
JMP_(stg_gc_enter_1);
FE_
}

static StgWord sm3_info[] = {
((W_)&sm3_srt+0), 0x0, 0x30016U
};

II_(rjQ_closure);
II_(sm2_info);
IF_(sm3_entry) {
FB_
if ((W_)(((W_)Sp - 0xcU) < (W_)SpLim)) goto _cmg;
Hp=Hp+4;
if ((W_)((W_)Hp > (W_)HpLim)) goto _cmi;
Hp[-3] = (W_)&stg_CAF_BLACKHOLE_info;
;EF_(newCAF);
{void (*ghcFunPtr)(void *);
ghcFunPtr = ((void (*)(void *))(W_)&newCAF);
ghcFunPtr((void *)R1.w);;}
R1.p[1] = (W_)Hp-12;
*R1.p = (W_)&stg_IND_STATIC_info;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = (W_)Hp-12;
Hp[-1] = (W_)&sm2_info;
R1.w = (W_)&rjQ_closure;
Sp[-3] = (W_)Hp-4;
Sp=Sp-3;
JMP_((W_)&stg_ap_p_fast);
_cmg:
JMP_(stg_gc_enter_1);
_cmi:
HpAlloc = 0x10U;
goto _cmg;
FE_
}
II_(rjO_closure);
II_(sm3_closure);
StgWord Main_main_srt[] = {
(W_)&rjO_closure, (W_)&sm3_closure
};

EI_(Main_main_info);
StgWord Main_main_closure[] = {
(W_)&Main_main_info, 0x0, 0x0, 0x0
};

StgWord Main_main_info[] = {
((W_)&Main_main_srt+0), 0x0, 0x30016U
};

II_(rjO_closure);
II_(sm3_closure);
FN_(Main_main_entry) {
FB_
if ((W_)(((W_)Sp - 0xcU) < (W_)SpLim)) goto _cms;
Hp=Hp+2;
if ((W_)((W_)Hp > (W_)HpLim)) goto _cmu;
Hp[-1] = (W_)&stg_CAF_BLACKHOLE_info;
;EF_(newCAF);
{void (*ghcFunPtr)(void *);
ghcFunPtr = ((void (*)(void *))(W_)&newCAF);
ghcFunPtr((void *)R1.w);;}
R1.p[1] = (W_)Hp-4;
*R1.p = (W_)&stg_IND_STATIC_info;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = (W_)Hp-4;
R1.w = (W_)&rjO_closure;
Sp[-3] = (W_)&sm3_closure;
Sp=Sp-3;
JMP_((W_)&stg_ap_p_fast);
_cms:
JMP_(stg_gc_enter_1);
_cmu:
HpAlloc = 0x8U;
goto _cms;
FE_
}
EI_(base_GHCziTopHandler_runMainIO_closure);
EI_(Main_main_closure);
StgWord ZCMain_main_srt[] = {
(W_)&base_GHCziTopHandler_runMainIO_closure, (W_)&Main_main_closure
};

EI_(ZCMain_main_info);
StgWord ZCMain_main_closure[] = {
(W_)&ZCMain_main_info, 0x0, 0x0, 0x0
};

StgWord ZCMain_main_info[] = {
((W_)&ZCMain_main_srt+0), 0x0, 0x30016U
};

EI_(base_GHCziTopHandler_runMainIO_closure);
EI_(Main_main_closure);
FN_(ZCMain_main_entry) {
FB_
if ((W_)(((W_)Sp - 0xcU) < (W_)SpLim)) goto _cmE;
Hp=Hp+2;
if ((W_)((W_)Hp > (W_)HpLim)) goto _cmG;
Hp[-1] = (W_)&stg_CAF_BLACKHOLE_info;
;EF_(newCAF);
{void (*ghcFunPtr)(void *);
ghcFunPtr = ((void (*)(void *))(W_)&newCAF);
ghcFunPtr((void *)R1.w);;}
R1.p[1] = (W_)Hp-4;
*R1.p = (W_)&stg_IND_STATIC_info;
Sp[-2] = (W_)&stg_upd_frame_info;
Sp[-1] = (W_)Hp-4;
R1.w = (W_)&base_GHCziTopHandler_runMainIO_closure;
Sp[-3] = (W_)&Main_main_closure;
Sp=Sp-3;
JMP_((W_)&stg_ap_p_fast);
_cmE:
JMP_(stg_gc_enter_1);
_cmG:
HpAlloc = 0x8U;
goto _cmE;
FE_
}
static StgWord _module_registered[] = {
0x0
};


EF_(__stginit_base_Prelude_);
EF_(__stginit_base_GHCziTopHandler_);
FN_(__stginit_Main_) {
FB_
if ((W_)(0x0 != (*((P_)(W_)&_module_registered)))) goto _cmM;
goto _cmO;
_cmM:
Sp=Sp+1;
JMP_(Sp[-1]);
_cmO:
*((P_)(W_)&_module_registered) = 0x1U;
Sp=Sp-1;
*Sp = (W_)&__stginit_base_Prelude_;
Sp=Sp-1;
*Sp = (W_)&__stginit_base_GHCziTopHandler_;
goto _cmM;
FE_
}


EF_(__stginit_Main_);
FN_(__stginit_Main) {
FB_
JMP_((W_)&__stginit_Main_);
FE_
}


FN_(__stginit_ZCMain) {
FB_
Sp=Sp+1;
JMP_(Sp[-1]);
FE_
}
