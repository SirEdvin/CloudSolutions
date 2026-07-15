import { printReports, TestResult, TestSuite } from "@siredvin/soteria";

export function runSuite(suite: TestSuite): void {
    const reports = suite.run();
    printReports(reports);
    for (const report of reports) {
        if (report.result != TestResult.SUCCESS) throw `${report.name}: ${report.result}: ${report.message}`;
    }
}
