.section .data
	.align 4
_rd9_srt:
	.long	_integerzmgmp_GHCziInteger_smallInteger_closure
.data
	.align 4
_rd9_closure:
	.long	_rd9_info
	.long	0
.text
	.align 4,0x90
	.long	_rd9_srt-(_skb_info)+0
	.long	0
	.long	65552
_skb_info:
.Lckx:
	leal -12(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .Lckz
	movl $_stg_upd_frame_info,-8(%ebp)
	movl %esi,-4(%ebp)
	movl $_integerzmgmp_GHCziInteger_smallInteger_closure,%esi
	movl $1,-12(%ebp)
	addl $-12,%ebp
	jmp _stg_ap_n_fast
.Lckz:
	jmp *-12(%ebx)
.text
	.align 4,0x90
	.long	_rd9_srt-(_sjW_info)+0
	.long	1
	.long	65553
_sjW_info:
.LckJ:
	leal -20(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .LckL
	addl $8,%edi
	cmpl 92(%ebx),%edi
	ja .LckN
	movl $_stg_upd_frame_info,-8(%ebp)
	movl %esi,-4(%ebp)
	movl $_skb_info,-4(%edi)
	leal -4(%edi),%eax
	movl %eax,-12(%ebp)
	movl $_stg_ap_p_info,-16(%ebp)
	movl 8(%esi),%eax
	movl %eax,-20(%ebp)
	addl $-20,%ebp
	jmp _base_GHCziNum_fromInteger_info
.LckN:
	movl $8,112(%ebx)
.LckL:
	jmp *-12(%ebx)
.text
	.align 4,0x90
	.long	_rd9_srt-(_skc_info)+0
	.long	0
	.long	65552
_skc_info:
.Lcl1:
	leal -12(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .Lcl3
	movl $_stg_upd_frame_info,-8(%ebp)
	movl %esi,-4(%ebp)
	movl $_integerzmgmp_GHCziInteger_smallInteger_closure,%esi
	movl $0,-12(%ebp)
	addl $-12,%ebp
	jmp _stg_ap_n_fast
.Lcl3:
	jmp *-12(%ebx)
.text
	.align 4,0x90
	.long	_rd9_srt-(_sjZ_info)+0
	.long	1
	.long	65553
_sjZ_info:
.Lcld:
	leal -20(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .Lclf
	addl $8,%edi
	cmpl 92(%ebx),%edi
	ja .Lclh
	movl $_stg_upd_frame_info,-8(%ebp)
	movl %esi,-4(%ebp)
	movl $_skc_info,-4(%edi)
	leal -4(%edi),%eax
	movl %eax,-12(%ebp)
	movl $_stg_ap_p_info,-16(%ebp)
	movl 8(%esi),%eax
	movl %eax,-20(%ebp)
	addl $-20,%ebp
	jmp _base_GHCziNum_fromInteger_info
.Lclh:
	movl $8,112(%ebx)
.Lclf:
	jmp *-12(%ebx)
.text
	.align 4,0x90
	.long	3
	.long	16
_skf_info:
.LclJ:
	leal -24(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .LclL
	movl $_stg_upd_frame_info,-8(%ebp)
	movl %esi,-4(%ebp)
	movl 12(%esi),%eax
	movl %eax,-12(%ebp)
	movl 16(%esi),%eax
	movl %eax,-16(%ebp)
	movl $_stg_ap_pp_info,-20(%ebp)
	movl 8(%esi),%eax
	movl %eax,-24(%ebp)
	addl $-24,%ebp
	jmp _base_GHCziNum_zm_info
.LclL:
	jmp *-12(%ebx)
.text
	.align 4,0x90
	.long	4
	.long	16
_skg_info:
.Lcm0:
	leal -12(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .Lcm2
	addl $20,%edi
	cmpl 92(%ebx),%edi
	ja .Lcm4
	movl $_stg_upd_frame_info,-8(%ebp)
	movl %esi,-4(%ebp)
	movl $_skf_info,-16(%edi)
	movl 8(%esi),%eax
	movl %eax,-8(%edi)
	movl 12(%esi),%eax
	movl %eax,-4(%edi)
	movl 16(%esi),%eax
	movl %eax,(%edi)
	movl 20(%esi),%esi
	leal -16(%edi),%eax
	movl %eax,-12(%ebp)
	addl $-12,%ebp
	jmp _sk5_info
.Lcm4:
	movl $20,112(%ebx)
.Lcm2:
	jmp *-12(%ebx)
.text
	.align 4,0x90
	.long	69
	.long	34
_ske_info:
.Lcmq:
	movl %esi,%eax
	andl $3,%eax
	cmpl $2,%eax
	jae .Lcmr
	addl $24,%edi
	cmpl 92(%ebx),%edi
	ja .Lcmv
	movl $_skg_info,-20(%edi)
	movl 16(%ebp),%eax
	movl %eax,-12(%edi)
	movl 12(%ebp),%eax
	movl %eax,-8(%edi)
	movl 20(%ebp),%eax
	movl %eax,-4(%edi)
	movl 4(%ebp),%eax
	movl %eax,(%edi)
	movl $_stg_ap_pp_info,12(%ebp)
	movl 16(%ebp),%eax
	movl %eax,8(%ebp)
	movl 20(%ebp),%eax
	movl %eax,16(%ebp)
	leal -20(%edi),%eax
	movl %eax,20(%ebp)
	addl $8,%ebp
	jmp _base_GHCziNum_zt_info
.Lcmr:
	movl 12(%ebp),%esi
	addl $24,%ebp
	jmp _stg_ap_0_fast
.Lcmv:
	movl $24,112(%ebx)
.Lcmt:
	jmp *-12(%ebx)
.text
	.align 4,0x90
	.long	5
	.long	34
_skd_info:
.LcmL:
	movl 8(%ebp),%eax
	movl %eax,-4(%ebp)
	movl 20(%ebp),%eax
	movl %eax,-8(%ebp)
	movl $_stg_ap_pp_info,-12(%ebp)
	movl %esi,-16(%ebp)
	movl $_ske_info,(%ebp)
	addl $-16,%ebp
	jmp _base_GHCziClasses_zeze_info
.text
	.align 4,0x90
	.long	65541
	.long	3
	.long	9
_sk5_info:
.LcmW:
	leal -36(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .LcmY
	movl %esi,-16(%ebp)
	movl 11(%esi),%eax
	movl %eax,-12(%ebp)
	movl 7(%esi),%eax
	movl %eax,-8(%ebp)
	movl 3(%esi),%eax
	movl %eax,-4(%ebp)
	movl 3(%esi),%eax
	movl %eax,-24(%ebp)
	movl $_skd_info,-20(%ebp)
	addl $-24,%ebp
	jmp _base_GHCziNum_zdp1Num_info
.LcmY:
	jmp *-8(%ebx)
.text
	.align 4,0x90
	.long	_rd9_srt-(_rd9_info)+0
	.long	65541
	.long	0
	.long	65551
_rd9_info:
.Lcnd:
	addl $40,%edi
	cmpl 92(%ebx),%edi
	ja .Lcnh
	movl $_sjW_info,-36(%edi)
	movl (%ebp),%eax
	movl %eax,-28(%edi)
	movl $_sjZ_info,-24(%edi)
	movl (%ebp),%eax
	movl %eax,-16(%edi)
	movl $_sk5_info,-12(%edi)
	movl (%ebp),%eax
	movl %eax,-8(%edi)
	leal -36(%edi),%eax
	movl %eax,-4(%edi)
	leal -24(%edi),%eax
	movl %eax,(%edi)
	leal -11(%edi),%esi
	addl $4,%ebp
	jmp *(%ebp)
.Lcnh:
	movl $40,112(%ebx)
.Lcnf:
	movl $_rd9_closure,%esi
	jmp *-8(%ebx)
.section .data
	.align 4
_rjM_srt:
	.long	_base_GHCziNum_zdfNumInteger_closure
.data
	.align 4
_rjM_closure:
	.long	_rjM_info
	.long	0
	.long	0
	.long	0
.text
	.align 4,0x90
	.long	_rjM_srt-(_rjM_info)+0
	.long	0
	.long	65558
_rjM_info:
.LcnE:
	leal -12(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .LcnG
	addl $8,%edi
	cmpl 92(%ebx),%edi
	ja .LcnI
	movl $_stg_CAF_BLACKHOLE_info,-4(%edi)
	pushl %esi
	call _newCAF
	addl $4,%esp
	leal -4(%edi),%eax
	movl %eax,4(%esi)
	movl $_stg_IND_STATIC_info,(%esi)
	movl $_stg_upd_frame_info,-8(%ebp)
	leal -4(%edi),%eax
	movl %eax,-4(%ebp)
	movl $_base_GHCziNum_zdfNumInteger_closure,-12(%ebp)
	addl $-12,%ebp
	jmp _base_GHCziNum_zdp2Num_info
.LcnI:
	movl $8,112(%ebx)
.LcnG:
	jmp *-12(%ebx)
.section .data
	.align 4
_rjO_srt:
	.long	_base_SystemziIO_print_closure
	.long	_rjM_closure
.data
	.align 4
_rjO_closure:
	.long	_rjO_info
	.long	0
	.long	0
	.long	0
.text
	.align 4,0x90
	.long	_rjO_srt-(_rjO_info)+0
	.long	0
	.long	196630
_rjO_info:
.Lco3:
	leal -12(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .Lco5
	addl $8,%edi
	cmpl 92(%ebx),%edi
	ja .Lco7
	movl $_stg_CAF_BLACKHOLE_info,-4(%edi)
	pushl %esi
	call _newCAF
	addl $4,%esp
	leal -4(%edi),%eax
	movl %eax,4(%esi)
	movl $_stg_IND_STATIC_info,(%esi)
	movl $_stg_upd_frame_info,-8(%ebp)
	leal -4(%edi),%eax
	movl %eax,-4(%ebp)
	movl $_base_SystemziIO_print_closure,%esi
	movl $_rjM_closure,-12(%ebp)
	addl $-12,%ebp
	jmp _stg_ap_p_fast
.Lco7:
	movl $8,112(%ebx)
.Lco5:
	jmp *-12(%ebx)
.section .data
	.align 4
_rjQ_srt:
	.long	_base_GHCziNum_zdfNumInteger_closure
	.long	_integerzmgmp_GHCziInteger_smallInteger_closure
.data
	.align 4
_rjQ_closure:
	.long	_rjQ_info
	.long	0
	.long	0
	.long	0
.text
	.align 4,0x90
	.long	_rjQ_srt-(_rjQ_info)+0
	.long	0
	.long	196630
_rjQ_info:
.Lcos:
	leal -12(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .Lcou
	addl $8,%edi
	cmpl 92(%ebx),%edi
	ja .Lcow
	movl $_stg_CAF_BLACKHOLE_info,-4(%edi)
	pushl %esi
	call _newCAF
	addl $4,%esp
	leal -4(%edi),%eax
	movl %eax,4(%esi)
	movl $_stg_IND_STATIC_info,(%esi)
	movl $_stg_upd_frame_info,-8(%ebp)
	leal -4(%edi),%eax
	movl %eax,-4(%ebp)
	movl $_base_GHCziNum_zdfNumInteger_closure,-12(%ebp)
	addl $-12,%ebp
	jmp _rd9_info
.Lcow:
	movl $8,112(%ebx)
.Lcou:
	jmp *-12(%ebx)
.section .data
	.align 4
_soF_srt:
	.long	_integerzmgmp_GHCziInteger_smallInteger_closure
	.long	_rjQ_closure
.data
	.align 4
_soF_closure:
	.long	_soF_info
	.long	0
	.long	0
	.long	0
.text
	.align 4,0x90
	.long	_soF_srt-(_soE_info)+0
	.long	0
	.long	65552
_soE_info:
.LcoT:
	leal -12(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .LcoV
	movl $_stg_upd_frame_info,-8(%ebp)
	movl %esi,-4(%ebp)
	movl $_integerzmgmp_GHCziInteger_smallInteger_closure,%esi
	movl $3,-12(%ebp)
	addl $-12,%ebp
	jmp _stg_ap_n_fast
.LcoV:
	jmp *-12(%ebx)
.text
	.align 4,0x90
	.long	_soF_srt-(_soF_info)+0
	.long	0
	.long	196630
_soF_info:
.Lcp6:
	leal -12(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .Lcp8
	addl $16,%edi
	cmpl 92(%ebx),%edi
	ja .Lcpa
	movl $_stg_CAF_BLACKHOLE_info,-12(%edi)
	pushl %esi
	call _newCAF
	addl $4,%esp
	leal -12(%edi),%eax
	movl %eax,4(%esi)
	movl $_stg_IND_STATIC_info,(%esi)
	movl $_stg_upd_frame_info,-8(%ebp)
	leal -12(%edi),%eax
	movl %eax,-4(%ebp)
	movl $_soE_info,-4(%edi)
	movl $_rjQ_closure,%esi
	leal -4(%edi),%eax
	movl %eax,-12(%ebp)
	addl $-12,%ebp
	jmp _stg_ap_p_fast
.Lcpa:
	movl $16,112(%ebx)
.Lcp8:
	jmp *-12(%ebx)
.section .data
	.align 4
.globl _Main_main_srt
_Main_main_srt:
	.long	_rjO_closure
	.long	_soF_closure
.data
	.align 4
.globl _Main_main_closure
_Main_main_closure:
	.long	_Main_main_info
	.long	0
	.long	0
	.long	0
.text
	.align 4,0x90
	.long	_Main_main_srt-(_Main_main_info)+0
	.long	0
	.long	196630
.globl _Main_main_info
_Main_main_info:
.Lcpw:
	leal -12(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .Lcpy
	addl $8,%edi
	cmpl 92(%ebx),%edi
	ja .LcpA
	movl $_stg_CAF_BLACKHOLE_info,-4(%edi)
	pushl %esi
	call _newCAF
	addl $4,%esp
	leal -4(%edi),%eax
	movl %eax,4(%esi)
	movl $_stg_IND_STATIC_info,(%esi)
	movl $_stg_upd_frame_info,-8(%ebp)
	leal -4(%edi),%eax
	movl %eax,-4(%ebp)
	movl $_rjO_closure,%esi
	movl $_soF_closure,-12(%ebp)
	addl $-12,%ebp
	jmp _stg_ap_p_fast
.LcpA:
	movl $8,112(%ebx)
.Lcpy:
	jmp *-12(%ebx)
.section .data
	.align 4
.globl _ZCMain_main_srt
_ZCMain_main_srt:
	.long	_base_GHCziTopHandler_runMainIO_closure
	.long	_Main_main_closure
.data
	.align 4
.globl _ZCMain_main_closure
_ZCMain_main_closure:
	.long	_ZCMain_main_info
	.long	0
	.long	0
	.long	0
.text
	.align 4,0x90
	.long	_ZCMain_main_srt-(_ZCMain_main_info)+0
	.long	0
	.long	196630
.globl _ZCMain_main_info
_ZCMain_main_info:
.LcpV:
	leal -12(%ebp),%eax
	cmpl 84(%ebx),%eax
	jb .LcpX
	addl $8,%edi
	cmpl 92(%ebx),%edi
	ja .LcpZ
	movl $_stg_CAF_BLACKHOLE_info,-4(%edi)
	pushl %esi
	call _newCAF
	addl $4,%esp
	leal -4(%edi),%eax
	movl %eax,4(%esi)
	movl $_stg_IND_STATIC_info,(%esi)
	movl $_stg_upd_frame_info,-8(%ebp)
	leal -4(%edi),%eax
	movl %eax,-4(%ebp)
	movl $_base_GHCziTopHandler_runMainIO_closure,%esi
	movl $_Main_main_closure,-12(%ebp)
	addl $-12,%ebp
	jmp _stg_ap_p_fast
.LcpZ:
	movl $8,112(%ebx)
.LcpX:
	jmp *-12(%ebx)
.data
	.align 4
__module_registered:
	.long	0
.text
	.align 4,0x90
.globl ___stginit_Main_
___stginit_Main_:
.Lcqd:
	cmpl $0,__module_registered
	jne .Lcqe
.Lcqf:
	movl $1,__module_registered
	addl $-4,%ebp
	movl $___stginit_base_Prelude_,(%ebp)
	addl $-4,%ebp
	movl $___stginit_base_GHCziTopHandler_,(%ebp)
.Lcqe:
	addl $4,%ebp
	jmp *-4(%ebp)
.text
	.align 4,0x90
.globl ___stginit_Main
___stginit_Main:
.Lcql:
	jmp ___stginit_Main_
.text
	.align 4,0x90
.globl ___stginit_ZCMain
___stginit_ZCMain:
.Lcqq:
	addl $4,%ebp
	jmp *-4(%ebp)
.ident "GHC 6.12.3"
