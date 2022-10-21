package io.github.sergkhram.idbClient.interceptors

import io.github.sergkhram.idbClient.logs.KLogger
import io.grpc.*
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall

object IdbInterceptor: ClientInterceptor {
    private val log = KLogger.logger

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        next: Channel?
    ): ClientCall<ReqT, RespT> {
        return object : SimpleForwardingClientCall<ReqT, RespT>(
            next?.newCall(
                method,
                callOptions!!.withoutWaitForReady()
            )
        ) {
            override fun sendMessage(message: ReqT) {
                log.debug(
                    "sent by grpc " + method?.fullMethodName + " : " + message
                )
                super.sendMessage(message)
            }

            override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                val listener: Listener<RespT> = object : ForwardingClientCallListener<RespT>() {
                    override fun delegate(): Listener<RespT> {
                        return responseListener
                    }

                    override fun onMessage(message: RespT) {
                        log.debug("received by grpc" + method?.fullMethodName + ": " + message)
                        super.onMessage(message)
                    }
                }
                super.start(listener, headers)
            }
        }
    }
}