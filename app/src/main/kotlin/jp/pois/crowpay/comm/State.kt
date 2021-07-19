package jp.pois.crowpay.comm

enum class SenderState {
    SENT,
    INSERTED,
    FINISHED,
    ACCEPTED,
    CONNECTED
}

enum class ReceiverState {
    WAITING,
    RECEIVED,
    ACCEPTED,
    INSERT_FAILED,
    FINISHED,
    VERIFICATION_FAILED
}
