import axios from "axios";
import {User} from "./userService.js";

const API = `${import.meta.env.VITE_API_URL}/v1`;

export class TestResult {
    constructor(id, verdict, time, memory, message) {
        this.id = id;
        this.verdict = verdict;
        this.time = time;
        this.memory = memory;
        this.message = message;
    }

    // getParsedDescription() {
    //     return this.description
    //         .split('\n')
    //         .slice(0, -1)
    //         .map(el => el.split(';'))
    //         .map(test => ({
    //             submittedAt: new Date(test[0]),
    //             testName: test[1],
    //             passed: test[2] === 'True',
    //             time: parseFloat(test[3])
    //         }));
    // }

    static async getBySubtaskResultId(subtaskResultId) {
        const response = await axios.get(
            `${API}/subtaskResults/${subtaskResultId}/testResults`,
            User.fromLocalStorage().getAuthHeader()
        )

        return response.data
            .map(
                data => new TestResult(
                    data['id'],
                    data['verdict'],
                    data['time'],
                    data['memory'],
                    data['message']
                )
            );
    }
}
