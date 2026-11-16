package com.atiurin.ultron.allure.screenrecord

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScreenRecordProcessTest {
    private val ps = """
        PID ARGS
        1 /system/bin/init second_stage
        4321 screenrecord --time-limit 180 --bit-rate 2000000 /data/local/tmp/ultron_other_1.mp4
        4567 screenrecord --time-limit 180 --bit-rate 2000000 /data/local/tmp/ultron_demo_2.mp4
        4600 sh -c ps -A
    """.trimIndent()

    @Test
    fun `pid of the screenrecord writing our file is picked from ps output`() {
        assertEquals(4567, ScreenRecordProcess.findPid(ps, "/data/local/tmp/ultron_demo_2.mp4"))
    }

    @Test
    fun `pid is found in the toolbox ps format of API 23-25 where USER comes first`() {
        val toolboxPs = """
            USER      PID   PPID  VSIZE  RSS   WCHAN      PC        NAME
            root      1     0     8976   1432  SyS_epoll_ 00000000 S /init
            shell     4567  4560  12345  2345  SyS_epoll_ 00000000 S screenrecord --time-limit 180 --bit-rate 2000000 /data/local/tmp/ultron_demo_2.mp4
            shell     4600  4560  9999   1111  0          00000000 R ps
        """.trimIndent()
        assertEquals(4567, ScreenRecordProcess.findPid(toolboxPs, "/data/local/tmp/ultron_demo_2.mp4"))
    }

    @Test
    fun `other recordings on the device are left alone`() {
        assertNull(ScreenRecordProcess.findPid(ps, "/data/local/tmp/ultron_missing.mp4"))
    }

    @Test
    fun `a file name that is a prefix of another does not match`() {
        assertNull(ScreenRecordProcess.findPid(ps, "/data/local/tmp/ultron_demo_2.mp"))
    }

    @Test
    fun `test names become safe file names`() {
        assertEquals("/data/local/tmp/ultron_4242_com_x_DemoTest_clicks_1.mp4", ScreenRecordProcess.fileName("'com.x.DemoTest.clicks'", 1, "4242"))
    }

    /** Two instrumentation sessions on one device record a same-named test: each stops only its own `screenrecord`. */
    @Test
    fun `the same test and sequence in two processes get different files and pids`() {
        val ours = ScreenRecordProcess.fileName("demo", 2, "1001")
        val theirs = ScreenRecordProcess.fileName("demo", 2, "2002")
        assertNotEquals(ours, theirs)
        val bothRecording = """
            PID ARGS
            5001 screenrecord --time-limit 180 --bit-rate 2000000 $theirs
            5002 screenrecord --time-limit 180 --bit-rate 2000000 $ours
        """.trimIndent()
        assertEquals(5002, ScreenRecordProcess.findPid(bothRecording, ours))
        assertEquals(5001, ScreenRecordProcess.findPid(bothRecording, theirs))
    }

    @Test
    fun `command is a single shell-free invocation`() {
        val cmd = ScreenRecordProcess.command(ScreenRecordOptions(timeLimitSec = 60, bitRate = 1_000_000, size = "540x1200"), "/data/local/tmp/ultron_t.mp4")
        assertEquals("screenrecord --time-limit 60 --bit-rate 1000000 --size 540x1200 /data/local/tmp/ultron_t.mp4", cmd)
        assertEquals(
            "screenrecord --time-limit 180 --bit-rate 2000000 /data/local/tmp/ultron_t.mp4",
            ScreenRecordProcess.command(ScreenRecordOptions(), "/data/local/tmp/ultron_t.mp4")
        )
    }
}
