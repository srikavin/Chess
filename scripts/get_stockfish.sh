#
# Copyright 2021 Srikavin Ramkumar
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

curl "https://stockfishchess.org/files/stockfish_14_linux_x64.zip" -o /tmp/stockfish.zip
unzip /tmp/stockfish.zip -d /tmp/
ls /tmp/
export STOCKFISH_PATH=/tmp/stockfish_14_linux_x64/stockfish_14_x64
